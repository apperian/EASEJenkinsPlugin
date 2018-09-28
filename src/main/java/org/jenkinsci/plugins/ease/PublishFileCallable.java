package org.jenkinsci.plugins.ease;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.apperian.api.ApperianApi;
import com.apperian.api.ConnectionException;
import com.apperian.api.applications.Application;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningStatus;

import org.jenkinsci.remoting.RoleChecker;

import hudson.FilePath;
import hudson.Util;
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
        if (!upload.validateHasAuthFields()) {
            report("Error: The api token is not set and no stored credentials were found");
            return false;
        }

        if (!upload.isConfigurationValid()) {
            report("Error: all required upload parameters should be set: auth, appId and filename");
            return false;
        }

        String env = upload.getProdEnv();
        String customApperianUrl = upload.getCustomApperianUrl();
        String apiToken = credentialsManager.getCredentialWithId(upload.getApiTokenId());

        ApperianApi apperianApi = apperianApiFactory.create(env, customApperianUrl, apiToken);

        try {
            uploadApp(f, apperianApi);
        } catch (ConnectionException ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("General plugin problem : %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (upload.isSignApp()) {
                signApp(apperianApi);
            }
        } catch (ConnectionException ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error signing application: %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }

        try {
            if (upload.isEnableApp()) {
                enableApp(apperianApi);
            }
        } catch (ConnectionException ex) {
            logger.throwing("PublishFileCallable", "invoke", ex);
            report("Error enabling application: %s", ex);
            ex.printStackTrace(getLogger());
            return false;
        }

        return true;
    }

    public void setCredentialsManager(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    private void uploadApp(File appBinary, ApperianApi apperianApi) throws ConnectionException {

        String appId = new String(upload.getAppId());

        String author = null;
        if (!Utils.isEmptyString(upload.getAuthor())) {
            author = upload.getAuthor();
        }

        String version = null;
        if (!Utils.isEmptyString(upload.getVersion())) {
            version = upload.getVersion();
        }

        String versionNotes = null;
        if (!Utils.isEmptyString(upload.getVersionNotes())) {
            versionNotes = upload.getVersionNotes();
            Map<String, String> vars = new HashMap<>();

            vars.put("BUILD_TIMESTAMP", Utils.formatIso8601(new Date()));

            versionNotes = Util.replaceMacro(versionNotes, vars);
        }

        report("Updating application binary. Author: %s - Version: %s, Version Notes: %s", author, version, versionNotes);
        apperianApi.createNewVersion(appId, appBinary, author, version, versionNotes);
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

        if (signingStatus == SigningStatus.ERROR) {
            fail("Error signing the application: " + details);
        } else {
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
