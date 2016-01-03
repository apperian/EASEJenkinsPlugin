package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.application.UpdateApplicationMetadataResponse;
import com.apperian.api.metadata.Metadata;
import com.apperian.api.publishing.*;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.metadata.MetadataExtractor;

import com.apperian.api.signing.SignApplicationResponse;
import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.plugins.api.ApperianEaseEndpoint;

public class PublishFileCallable implements FilePath.FileCallable<Boolean>, Serializable {
    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private EaseUpload upload;
    private final BuildListener listener;

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.upload = upload;
        this.listener = listener;
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

        Metadata metadataUpdate = extractMetadataFromFile(applicationPackage);

        if (!Utils.isEmptyString(upload.getAuthor())) {
            metadataUpdate.setAuthor(upload.getAuthor());
        }

        String versionNotes = upload.getVersionNotes();
        if (!Utils.isEmptyString(versionNotes)) {
            Map<String, String> vars = new HashMap<>();

            vars.put("APP_NAME", Utils.override(
                    metadataUpdate.getName(),
                    metadata.getName()));

            vars.put("APP_VERSION", Utils.override(
                    metadataUpdate.getVersion(),
                    metadata.getVersion()));

            vars.put("BUILD_TIMESTAMP", Utils.formatIso8601(
                    new Date()));

            versionNotes = Util.replaceMacro(versionNotes, vars);

            metadataUpdate.setVersionNotes(versionNotes);
        }

        assignMetadata(metadata, metadataUpdate);

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

    private void assignMetadata(Metadata metadata, Metadata metadataUpdate) {
        for (String key : metadataUpdate.getValues().keySet()) {
            String value = metadataUpdate.getValues().get(key);
            report("Setting %s metadata to '%s'", key, value);
            metadata.getValues().put(key, value);
        }
    }

    private boolean signApp(File applicationPackage,
                            ApperianEndpoint apperianEndpoint) {

        if (!upload.isSign()) {
            return false;
        }
        if (Utils.trim(upload.getCredential()).isEmpty()) {
            report("Failed to sign: no credentials set");
            return false;
        }


        report("Signing application %s", upload.getCredential());
        try {
            ApperianResourceID appId = new ApperianResourceID(upload.getAppId());
            ApperianResourceID credentialId = new ApperianResourceID(upload.getCredential());


            SignApplicationResponse response;
            response = ApperianEaseApi.SIGNING.signApplication(credentialId, appId)
                    .call(apperianEndpoint);

            if (response.hasError()) {
                report("Error enabling application: %s", response.getErrorMessage());
                return false;
            }

        } catch (IOException e) {
            report("Network error: %s", e.getMessage());
            return false;
        }

        return true;
    }

    private boolean enableApp(File applicationPackage,
                              ApperianEndpoint apperianEndpoint) {

        if (!upload.isEnable()) {
            return false;
        }

        report("Enabling application");
        try {
            ApperianResourceID appId = new ApperianResourceID(upload.getAppId());

            UpdateApplicationMetadataResponse response;
            response = ApperianEaseApi.APPLICATIONS.updateApplicationMetadata(appId)
                    .setEnabled(true)
                    .call(apperianEndpoint);

            if (response.hasError()) {
                report("Error enabling application: %s", response.getErrorMessage());
                return false;
            }

        } catch (IOException e) {
            report("Network error: %s", e.getMessage());
            return false;
        }

        return true;
    }

    private Metadata extractMetadataFromFile(File file) {
        report("Extracting from dist archive '%s'", file.getName());

        Metadata metadata = new Metadata(new HashMap<String, String>());

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

        return metadata;
    }

    public EaseUpload getUpload() {
        return upload;
    }

    public PrintStream getLogger() {
        return listener.getLogger();
    }

    private void report(String message, Object ...args) {
        getLogger().println(String.format(message, args));
    }

}
