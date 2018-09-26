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

import org.jenkinsci.plugins.api.ApiConnection;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.application.Application;
import com.apperian.api.application.GetApplicationInfoResponse;
import com.apperian.api.application.UpdateApplicationResponse;
import com.apperian.api.application.Application.Version;
import com.apperian.api.metadata.Metadata;
import com.apperian.api.metadata.MetadataExtractor;
import com.apperian.api.publishing.UploadResult;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningStatus;

import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

public class PublishFileCallable implements FilePath.FileCallable<Boolean> {
    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private EaseUpload upload;
    private final BuildListener listener;
    private transient ApiManager apiManager = new ApiManager();
    private transient CredentialsManager credentialsManager = new CredentialsManager();

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.upload = upload;
        this.listener = listener;
    }

    public void checkRoles(RoleChecker var1) throws SecurityException {

    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        if (!upload.validateHasAuthFields()) {
            report("Error: The api token is not set and no stored credentials were found");
            return false;
        }

        if (!upload.checkOk()) {
            report("Error: all required upload parameters should be set: auth, appId and filename");
            return false;
        }

        String env = upload.prodEnv;
        String customEaseUrl = upload.customEaseUrl;
        String customApperianUrl = upload.customApperianUrl;
        String apiToken = credentialsManager.getCredentialWithId(upload.apiTokenId);

        ApiConnection apiConnection = apiManager.createConnection(env, customEaseUrl, customApperianUrl, apiToken);

        ApperianEndpoint apperianEndpoint = apiConnection.getApperianEndpoint();

        try {
            uploadApp(f, apperianEndpoint);
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("General plugin problem : %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (upload.signApp) {
                signApp(f, apperianEndpoint);
            }
        } catch (Exception ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error signing application: %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (upload.enableApp) {
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

    private void uploadApp(File appBinary, ApperianEndpoint apperianEndpoint) throws ConnectionException {

        ApperianResourceID appId = new ApperianResourceID(upload.appId);

        String author = null;
        if (!Utils.isEmptyString(upload.author)) {
            author = upload.author;
        }

        String version = null;
        if (!Utils.isEmptyString(upload.version)) {
            version = upload.version;
        }

        String versionNotes = null;
        if (!Utils.isEmptyString(upload.versionNotes)) {
            versionNotes = upload.versionNotes;
            Map<String, String> vars = new HashMap<>();

            vars.put("BUILD_TIMESTAMP", Utils.formatIso8601(new Date()));

            versionNotes = Util.replaceMacro(versionNotes, vars);
        }

        report("Updating application binary. Author: %s - Version: %s, Version Notes: %s", author, version, versionNotes);
        ApperianEaseApi.APPLICATIONS.updateApplication(apperianEndpoint, appId, appBinary, author, version, versionNotes);
    }

    private void signApp(File applicationPackage,
                         ApperianEndpoint apperianEndpoint) throws ConnectionException {
        report("Signing application with credential '%s'", upload.credential);
        ApperianResourceID appId = new ApperianResourceID(upload.appId);
        ApperianResourceID credentialId = new ApperianResourceID(upload.credential);


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

            GetApplicationInfoResponse appInfoResponse = ApperianEaseApi.APPLICATIONS.getApplicationInfo(apperianEndpoint, appId);

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
                           ApperianEndpoint apperianEndpoint) throws ConnectionException {
        report("Enabling application with ID '%s'", upload.appId);
        ApperianResourceID appId = new ApperianResourceID(upload.appId);

        ApperianEaseApi.APPLICATIONS.updateApplication(apperianEndpoint, appId, true);
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
