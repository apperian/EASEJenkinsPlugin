package org.jenkinsci.plugins.ease;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.apperian.api.ApperianApi;
import com.apperian.api.ConnectionException;
import com.apperian.api.applications.Application;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningStatus;

import org.jenkinsci.remoting.RoleChecker;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

public class PublishFileCallable implements FilePath.FileCallable<Boolean> {
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(PublishFileCallable.class.getName());

    private EaseUpload upload;
    private final BuildListener listener;
    private transient ApperianApiFactory apperianApiFactory = new ApperianApiFactory();
    private transient CredentialsManager credentialsManager = new CredentialsManager();

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.upload = upload;
        this.listener = listener;
    }

    public void checkRoles(RoleChecker var1) throws SecurityException {

    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        try {
            upload.checkHasAuthFields();
        } catch (Exception e) {
            report("Error in the fields for authentication. " + e.getMessage());
            return false;
        }

        try {
            upload.checkConfiguration();
        } catch (Exception e) {
            report("Error in the configuration. " + e.getMessage());
            return false;
        }

        String env = upload.getProdEnv();
        String customApperianUrl = upload.getCustomApperianUrl();
        String apiToken = credentialsManager.getCredentialWithId(upload.getApiTokenId());

        ApperianApi apperianApi = apperianApiFactory.create(env, customApperianUrl, apiToken);

        // When we publish the app we only enable it if it needs to be enabled and no signing is needed.
        boolean enableAppOnPublishing = (!upload.isSignApp()) && upload.isEnableApp();
        // When signing is needed we publish it as disabled and then after signing it we enable the app.
        boolean enableAppAfterSigning = upload.isSignApp() && upload.isEnableApp();

        try {
            uploadApp(f, apperianApi, enableAppOnPublishing);
        } catch (ConnectionException ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("General plugin problem. Message: %s. Error details: %s.", ex.getMessage(), ex.getErrorDetails());
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (upload.isSignApp()) {
                signApp(apperianApi);
            }
        } catch (ConnectionException ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error signing application. Message: %s. Error details: %s.", ex.getMessage(), ex.getErrorDetails());
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (enableAppAfterSigning) {
                enableApp(apperianApi);
            }
        } catch (ConnectionException ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error enabling application. Message: %s. Error details: %s.", ex.getMessage(), ex.getErrorDetails());
            ex.printStackTrace(getLogger());
            return false;
        }

        return true;
    }

    public void setCredentialsManager(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    private void uploadApp(File appBinary, ApperianApi apperianApi, boolean enableApp) throws ConnectionException {

        String appId = new String(upload.getAppId());

        String author = null;
        if (!Utils.isEmptyString(upload.getAuthor())) {
            author = upload.applyEnvVariablesFormatter(upload.getAuthor());
        }

        String version = null;
        if (!Utils.isEmptyString(upload.getVersion())) {
            version = upload.applyEnvVariablesFormatter(upload.getVersion());
        }

        String versionNotes = null;
        if (!Utils.isEmptyString(upload.getVersionNotes())) {
            versionNotes = upload.applyEnvVariablesFormatter(upload.getVersionNotes());
        }

        report("Updating application binary. Author: %s - Version: %s, Version Notes: %s, Enabled: %b",
               author, version, versionNotes, enableApp);
        apperianApi.createNewVersion(appId, appBinary, author, version, versionNotes, enableApp);
    }

    private void signApp(ApperianApi apperianApi) throws ConnectionException {
        report("Signing application with credential '%s'", upload.getCredential());
        String appId = new String(upload.getAppId());
        String credentialId = new String(upload.getCredential());

        SignApplicationResponse response = apperianApi.signApplication(credentialId, appId);

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

            Application application = apperianApi.getApplicationInfo(appId);
            if (application == null || application.getVersion() == null) {
                throw new RuntimeException("Failed to get application " + appId + " signigng status");
            }

            signingStatus = application.getVersion().getStatus();
            details = getStatusDetails(application);
        }

        if (signingStatus == SigningStatus.SIGNED) {
            report(details);
        } else {
            fail("Error signing the application: " + details);
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

    private void enableApp(ApperianApi apperianApi) throws ConnectionException {
        report("Enabling application with ID '%s'", upload.getAppId());
        String appId = new String(upload.getAppId());

        apperianApi.updateApplication(appId, true);
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
