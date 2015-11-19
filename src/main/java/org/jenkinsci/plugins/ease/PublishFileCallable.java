package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import com.apperian.eas.publishing.*;
import com.apperian.eas.EASEEndpoint;
import com.apperian.eas.metadata.MetadataExtractor;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import hudson.util.Secret;

public class PublishFileCallable implements FilePath.FileCallable<Boolean>, Serializable {
    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private final BuildListener listener;
    private final String appId;
    private final String username;
    private final Secret password;
    private final String url;

    private final Map<String, String> metadataAssignment;

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.listener = listener;

        this.url = upload.getUrl();
        this.username = upload.getUsername();
        this.password = Secret.fromString(upload.getPassword());
        this.appId = upload.getAppId();
        this.metadataAssignment = Utils.parseAssignmentMap(upload.getMetadataAssignment());
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        try (EASEEndpoint endpoint = new EASEEndpoint(url)) {
            return publishFileToEndpoint(f, endpoint);
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("General plugin problem : %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }
    }

    private Boolean publishFileToEndpoint(File f, EASEEndpoint endpoint) throws IOException {
        EaseCredentials credentials = new EaseCredentials(url, username, password);
        try {
            credentials.lookupStoredCredentials();
        } catch (Exception ex) {
            logger.throwing(PublishFileCallable.class.getName(),
                            "publishFileToEndpoint",
                            ex);
            // but pass-through
        }

        if (!credentials.checkOk()) {
            report("Error: username/password are not set and there is no stored credentials found");
            return false;
        }

        AuthenticateUserResponse auth = credentials.authenticate(endpoint);
        if (auth.hasError()) {
            String errorMessage = auth.getErrorMessage();
            report("Error: %s, url=%s", errorMessage, url);
            return false;
        }

        UpdateResponse update = Publishing.API.update(auth.result.token, appId)
                .call(endpoint);

        if (update.hasError()) {
            String errorMessage = update.getErrorMessage();
            report("Error: %s, appId=%s", errorMessage, appId);
            return false;
        }

        Metadata metadata = update.result.EASEmetadata;

        report("Metadata from server: %s", metadata);

        assignAuthor(metadata);
        extractMetadataFromFile(metadata, f);
        assignUserSetVars(metadata, metadataAssignment);

        report("New metadata: %s", metadata);

        report("Publishing %s to EASE", f);
        UploadResult upload = endpoint.uploadFile(update.result.fileUploadURL, f);
        if (upload.hasError()) {
            report("Error: %s", upload.errorMessage);
            return false;
        }

        if (upload.fileID == null) {
            report("Error: Upload file ID is null. Publish transaction not finished");
            return false;
        }


        PublishResponse publish = Publishing.API.publish(auth.result.token, update.result.transactionID, metadata, upload.fileID).call(endpoint);
        if (publish.hasError()) {
            String errorMessage = publish.getErrorMessage();
            report(errorMessage);
            return false;
        }

        if (!appId.equals(publish.result.appID)) {
            report("Error: File uploaded but confirmational appId is wrong");
            return false;
        }

        report("DONE! Uploaded %s to %s for appId=%s", f.getName(), url, appId);
        return true;
    }

    private void assignAuthor(Metadata metadata) {
        report("Using 'username' for author property in metadata");
        metadata.setAuthor(getUsername());
    }

    private void assignUserSetVars(Metadata metadata, Map<String, String> map) {
        report("Taking from user provided metadata assignment");
        for (String name : map.keySet()) {
            String value = map.get(name);

            report("Assigning %s = '%s'", name, value);
            metadata.getValues().put(name, value);
        }
    }

    private void extractMetadataFromFile(Metadata metadata, File file) {
        report("Extracting from dist archive '%s'", file.getName());

        boolean extracted = false;
        for (MetadataExtractor extractor : MetadataExtractor.allExtractors(file)) {
            if (extractor.extractTo(metadata, file, getLogger())) {
                extracted = true;
                break;
            }
        }
        if (!extracted) {
            report("Couldn't find metadata extractor for '%s'", file.getName());
        }
    }

    public String getAppId() {
        return appId;
    }

    public String getUsername() {
        return username;
    }

    public Secret getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getMetadataAssignment() {
        return metadataAssignment;
    }

    public PrintStream getLogger() {
        return listener.getLogger();
    }

    private void report(String message, Object ...args) {
        getLogger().println(String.format(message, args));
    }

}
