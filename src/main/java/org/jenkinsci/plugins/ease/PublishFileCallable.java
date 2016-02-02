package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jenkinsci.plugins.api.ApperianEaseEndpoint;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.application.Application;
import com.apperian.api.application.GetApplicationInfoResponse;
import com.apperian.api.application.UpdateApplicationMetadataResponse;
import com.apperian.api.metadata.Metadata;
import com.apperian.api.metadata.MetadataExtractor;
import com.apperian.api.publishing.PublishApplicationResponse;
import com.apperian.api.publishing.UpdateApplicationResponse;
import com.apperian.api.publishing.UploadResult;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningStatus;

import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

public class PublishFileCallable implements FilePath.FileCallable<Boolean>, Serializable {
    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private EaseUpload upload;
    private final BuildListener listener;

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.upload = upload;
        this.listener = listener;
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        if (!upload.validateHasAuthFields()) {
            report("Error: username/password are not set and there is no stored credentials found");
            return false;
        }

        if (!upload.checkOk()) {
            report("Error: all required upload parameters should be set: auth, appId and filename");
            return false;
        }

        boolean shouldAuthApperian = upload.isEnableApp() || upload.isSignApp();

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

        ApperianEndpoint apperianEndpoint = endpoint.getApperianEndpoint();

        try {
            if (upload.isSignApp()) {
                signApp(f, apperianEndpoint);
            }
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error signing application: %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (upload.isEnableApp()) {
                enableApp(f, apperianEndpoint);
            }
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error enabling application: %s", ex);
            ex.printStackTrace(getLogger());
            return false;
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

        Metadata metadataUpdate = new Metadata(new HashMap<String, String>());

        if (!Utils.isEmptyString(upload.getAuthor())) {
            metadataUpdate.setAuthor(upload.getAuthor());
        }

        String versionNotes = upload.getVersionNotes();
        if (!Utils.isEmptyString(versionNotes)) {
            Map<String, String> vars = new HashMap<>();

            vars.put("BUILD_TIMESTAMP", Utils.formatIso8601(
                    new Date()));

            versionNotes = Util.replaceMacro(versionNotes, vars);

            metadataUpdate.setVersionNotes(versionNotes);
        }

        report("Metadata update: %s", metadataUpdate);

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


        PublishApplicationResponse publish = ApperianEaseApi.PUBLISHING.publish(update.result.transactionID, metadataUpdate, uploadResult.fileID)
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

    private void signApp(File applicationPackage,
                         ApperianEndpoint apperianEndpoint) throws IOException {
        report("Signing application with credential '%s'", upload.getCredential());
        ApperianResourceID appId = new ApperianResourceID(upload.getAppId());
        ApperianResourceID credentialId = new ApperianResourceID(upload.getCredential());


        SignApplicationResponse response = ApperianEaseApi.SIGNING.signApplication(credentialId, appId)
                                          .call(apperianEndpoint);

        if (response.hasError()) {
            throw new RuntimeException(response.getErrorMessage());
        }

        SigningStatus signingStatus = response.getStatus();
        String details = response.getStatusDetails();

        if (signingStatus == SigningStatus.IN_PROGRESS) {
            report("The application is being signed. Doing polling of signing status.");
        }

        long interval = 5;
        while (signingStatus == SigningStatus.IN_PROGRESS) {
            try {
                report("Sleeping " + interval + " seconds");
                Thread.sleep(TimeUnit.SECONDS.toMillis(interval));

                interval = interval + interval * 18 / 10;
                if (interval > 30) {
                    interval = 30;
                }
            } catch (InterruptedException e) {
                break;
            }

            GetApplicationInfoResponse appInfoResponse;
            appInfoResponse = ApperianEaseApi.APPLICATIONS.getApplicationInfo(appId)
                                                          .call(apperianEndpoint);

            if (appInfoResponse.hasError()) {
                throw new RuntimeException(appInfoResponse.getErrorMessage());
            }

            Application application = appInfoResponse.getApplication();
            if (application == null || application.getVersion() == null) {
                throw new RuntimeException("Failed to get application " + appId + " signigng status");
            }

            signingStatus = application.getVersion().getStatus();
            details = getStatusDetails(application);
            report(details);
        }
    }

    private String getStatusDetails(Application application) {
        SigningStatus signingStatus = application.getVersion().getStatus();
        String details = application.getVersion().getStatusDetails();

        if (!Utils.isEmptyString(details)) {
            return details;
        }

        switch (signingStatus) {
            case SIGNED: return "The application was signed successfully.";
            case CANCELLED: return "The signing request was cancelled.";
            case ERROR: return "Some error happened while signing.";
            case IN_PROGRESS: return "The application is being signed.";
        }

        return details;
    }

    private void enableApp(File applicationPackage,
                           ApperianEndpoint apperianEndpoint) throws IOException {
        report("Enabling application with ID '%s'", upload.getAppId());
        ApperianResourceID appId = new ApperianResourceID(upload.getAppId());

        UpdateApplicationMetadataResponse response;
        response = ApperianEaseApi.APPLICATIONS.updateApplicationMetadata(appId)
                .setEnabled(true)
                .call(apperianEndpoint);

        if (response.hasError()) {
            throw new RuntimeException(response.getErrorMessage());
        }
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
