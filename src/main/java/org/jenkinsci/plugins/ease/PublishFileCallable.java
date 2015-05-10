package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import com.apperian.eas.AuthenticateUserResponse;
import com.apperian.eas.Metadata;
import com.apperian.eas.PublishResponse;
import com.apperian.eas.PublishingAPI;
import com.apperian.eas.PublishingEndpoint;
import com.apperian.eas.UpdateResponse;
import com.apperian.eas.UploadResult;
import com.apperian.eas.metadata.MetadataExtractor;
import com.google.common.base.Splitter;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

public class PublishFileCallable implements FilePath.FileCallable<Boolean>, Serializable {
    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private final BuildListener listener;
    private final String appId;
    private final String username;
    private final String password;
    private final String url;
    private final String metadataAssignment;

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.listener = listener;

        this.url = upload.getUrl();
        this.username = upload.getUsername();
        this.password = upload.getPassword();
        this.appId = upload.getAppId();
        this.metadataAssignment = upload.getMetadataAssignment();
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        PublishingEndpoint endpoint = new PublishingEndpoint(url);
        try {
            return publishFileToEndpoint(f, endpoint);
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("General plugin problem: %s", ex);
            return false;
        } finally {
            endpoint.close();
        }
    }

    private Boolean publishFileToEndpoint(File f, PublishingEndpoint endpoint) throws IOException {
        EaseCredentials credentials = new EaseCredentials(url, username, password);
        credentials.lookupStoredCredentials();
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

        UpdateResponse update = PublishingAPI.update(auth.result.token, appId)
                .call(endpoint);

        if (update.hasError()) {
            String errorMessage = update.getErrorMessage();
            report("Error: %s, appId=%s", errorMessage, appId);
            return false;
        }

        Metadata metadata = update.result.EASEmetadata;

        report("Metadata from server: %s", metadata);

        extractMetadataFromFile(metadata, f);
        extractMetadataFromAssignment(metadata, metadataAssignment);

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


        PublishResponse publish = PublishingAPI.publish(
                auth.result.token,
                update.result.transactionID,
                metadata,
                upload.fileID).call(endpoint);
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

    private void extractMetadataFromAssignment(Metadata metadata, String metadataAssignment) {
        Map<String, String> map = Splitter.on(";")
                                            .trimResults()
                                            .withKeyValueSeparator("=")
                                            .split(metadataAssignment);

        report("Extracting from user provided metadata assignment");
        for (String name : map.keySet()) {
            String value = map.get(name);

            report("Extracted %s = '%s'", name, value);
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

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public PrintStream getLogger() {
        return listener.getLogger();
    }

    private void report(String message, Object ...args) {
        getLogger().println(String.format(message, args));
    }

}
