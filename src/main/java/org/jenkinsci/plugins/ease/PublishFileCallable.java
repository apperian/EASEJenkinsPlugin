package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.ApperianEndpoint;
import com.apperian.api.metadata.Metadata;
import com.apperian.api.publishing.*;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.metadata.MetadataExtractor;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import hudson.util.Secret;
import org.jenkinsci.plugins.api.ApperianEaseEndpoint;

public class PublishFileCallable implements FilePath.FileCallable<Boolean>, Serializable {
    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private EaseUpload upload;
    private final BuildListener listener;
    private final Map<String, String> metadataAssignment;

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.upload = upload;
        this.listener = listener;
        this.metadataAssignment = Utils.parseAssignmentMap(upload.getMetadataAssignment());
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        if (!upload.checkHasFieldsForAuth()) {
            report("Error: username/password are not set and there is no stored credentials found");
            return false;
        }

        if (!upload.checkOk()) {
            report("Error: all required upload parameters should be set: auth, appId and filename");
            return false;
        }

        boolean shouldAuthApperian = upload.isEnable() || upload.isSign();

        StringBuilder errorMessage = new StringBuilder();
        ApperianEaseEndpoint endpoint = upload.tryAuthenticate(true,
                shouldAuthApperian,
                errorMessage);

        if (endpoint == null) {
            report("Error: %s, endpoint=%s", errorMessage, upload.createEndpoint());
            return false;
        }

        try (EASEEndpoint easeEndpoint = endpoint.getEaseEndpoint()) {
            if (!uploadApp(f, easeEndpoint)) {
                return false;
            }
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("General plugin problem : %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }


        if (shouldAuthApperian) {
            try (ApperianEndpoint apperianEndpoint = endpoint.getApperianEndpoint()) {
                if (!signApp(f, apperianEndpoint)) {
                    return false;
                }
            } catch (Exception ex) {
                logger.throwing("PublishFileCallable", "invoke", ex);
                report("General plugin problem : %s", ex);
                ex.printStackTrace(getLogger());
                return false;
            }
        }
        return true;
    }

    private boolean uploadApp(File applicationPackage,
                              EASEEndpoint endpoint) throws IOException {

        String appId = upload.getAppId();

        UpdateApplicationResponse update = ApperianEaseApi.PUBLISHING.update(appId)
                .call(endpoint);

        if (update.hasError()) {
            String errorMessage = update.getErrorMessage();
            report("Error: %s, appId=%s", errorMessage, appId);
            return false;
        }

        Metadata metadata = update.result.EASEmetadata;

        report("Metadata from server: %s", metadata);

        assignAuthor(metadata);
        extractMetadataFromFile(metadata, applicationPackage);
        assignUserSetVars(metadata, metadataAssignment);

        report("New metadata: %s", metadata);

        report("Publishing %s to EASE", applicationPackage);
        UploadResult uploadResult = endpoint.uploadFile(update.result.fileUploadURL, applicationPackage);
        if (uploadResult.hasError()) {
            report("Error: %s", uploadResult.errorMessage);
            return false;
        }

        if (uploadResult.fileID == null) {
            report("Error: Upload file ID is null. Publish transaction not finished");
            return false;
        }


        PublishApplicationResponse publish = ApperianEaseApi.PUBLISHING.publish(update.result.transactionID, metadata, uploadResult.fileID)
                .call(endpoint);
        if (publish.hasError()) {
            String errorMessage = publish.getErrorMessage();
            report(errorMessage);
            return false;
        }

        if (!appId.equals(publish.result.appID)) {
            report("Error: File uploaded but confirmational appId is wrong");
            return false;
        }

        report("DONE! Uploaded %s to %s for appId=%s", applicationPackage.getName(), endpoint, appId);
        return true;
    }

    private void assignAuthor(Metadata metadata) {
        report("Using 'username' for author property in metadata");
        metadata.setAuthor(upload.getUsername());
    }

    private void assignUserSetVars(Metadata metadata, Map<String, String> map) {
        report("Taking from user provided metadata assignment");
        for (String name : map.keySet()) {
            String value = map.get(name);

            report("Assigning %s = '%s'", name, value);
            metadata.getValues().put(name, value);
        }
    }

    private boolean signApp(File applicationPackage,
                            ApperianEndpoint apperianEndpoint) {

        if (!upload.isSign()) {
            return false;
        }

        // TODO

        return true;
    }

    private boolean enableApp(File applicationPackage,
                              ApperianEndpoint apperianEndpoint) {

        if (!upload.isEnable()) {
            return false;
        }

        // TODO

        return true;
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

    public EaseUpload getUpload() {
        return upload;
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
