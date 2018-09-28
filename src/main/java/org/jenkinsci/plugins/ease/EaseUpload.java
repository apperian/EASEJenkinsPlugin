package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.apperian.api.ApperianApi;
import com.apperian.api.ConnectionException;
import com.apperian.api.applications.AppType;
import com.apperian.api.applications.Application;
import com.apperian.api.applications.Application.Version;
import com.apperian.api.signing.PlatformType;
import com.apperian.api.signing.SigningCredential;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

public class EaseUpload implements Describable<EaseUpload>, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(EaseUpload.class.getName());

    public String prodEnv;
    public String customApperianUrl;
    public String appId;
    public String filename;
    public String apiTokenId;
    public String author;
    public String version;
    public String versionNotes;
    public boolean signApp;
    public String credential;
    public boolean enableApp;

    public FilePath filePath;

    @DataBoundConstructor
    public EaseUpload(
            String prodEnv,
            String customApperianUrl,
            String apiTokenId,
            String appId,
            String filename,
            String author,
            String version,
            String versionNotes,
            boolean signApp,
            String credential,
            boolean enableApp) {
        this.prodEnv = Utils.trim(prodEnv);
        this.customApperianUrl = Utils.trim(customApperianUrl);

        this.apiTokenId = Utils.trim(apiTokenId);
        this.appId = Utils.trim(appId);
        this.filename = Utils.trim(filename);
        this.author = Utils.trim(author);
        this.version = Utils.trim(version);
        this.versionNotes = Utils.trim(versionNotes);
        this.signApp = signApp;
        this.credential = credential;
        this.enableApp = enableApp;
    }

    public static EaseUpload simpleUpload(
            @QueryParameter("prodEnv") String prodEnv,
            @QueryParameter("customApperianUrl") String customApperianUrl,
            @QueryParameter("apiTokenId") String apiTokenId) {

        return new EaseUpload(prodEnv,
                customApperianUrl,
                apiTokenId,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                false);
    }

    public boolean isConfigurationValid() {
        return !Utils.isEmptyString(appId) &&
                validateHasAuthFields() &&
                !Utils.isEmptyString(filename);
    }

    public boolean searchWorkspace(FilePath workspacePath,
                                   PrintStream buildLog) throws IOException, InterruptedException {
        FilePath[] paths = workspacePath.list(this.filename);
        if (paths.length != 1) {
            buildLog.println("Found " + (paths.length == 0 ? "no files" : " ambiguous list " + Arrays.asList(paths)) +
                    " as candidates for pattern '" + this.filename + "'");
            return false;
        }

        this.filePath = paths[0];
        return true;
    }

    public boolean validateHasAuthFields() {
        if (Utils.isEmptyString(apiTokenId)) {
            return false;
        }

        if (Utils.isEmptyString(this.prodEnv)) {
            return false;
        }

        ProductionEnvironment productionEnvironment = ProductionEnvironment.fromNameOrNA(this.prodEnv);
        if (productionEnvironment == null) {
            return false;
        }

        if (productionEnvironment == ProductionEnvironment.CUSTOM) {
            if (!Utils.isValidURL(customApperianUrl)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Descriptor<EaseUpload> getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<EaseUpload> {

        private transient ApperianApiFactory apperianApiFactory = new ApperianApiFactory();
        private transient CredentialsManager credentialsManager = new CredentialsManager();

        @Override
        public String getDisplayName() {
            return "Apperian Upload";
        }

        public ListBoxModel doFillProdEnvItems() {
            ListBoxModel resultListBox = new ListBoxModel();
            for (ProductionEnvironment prodEnv : ProductionEnvironment.values()) {
                resultListBox.add(prodEnv.getTitle(), prodEnv.name());
            }
            return resultListBox;
        }

        public ListBoxModel doFillApiTokenIdItems() {
            ListBoxModel resultListBox = new ListBoxModel();
            CredentialsManager credentialsManager = new CredentialsManager();
            List<ApiToken> credentials = credentialsManager.getCredentials();
            resultListBox.add("<Select an API token>", "");
            for (ApiToken easeUser : credentials) {
                resultListBox.add(easeUser.getDescription(), easeUser.getApiTokenId());
            }
            return resultListBox;
        }

        public ListBoxModel doFillAppIdItems(@QueryParameter("prodEnv") final String prodEnv,
                                             @QueryParameter("customApperianUrl") String customApperianUrl,
                                             @QueryParameter("apiTokenId") final String apiTokenId) {
            EaseUpload upload = EaseUpload.simpleUpload(prodEnv, customApperianUrl, apiTokenId);

            if (!upload.validateHasAuthFields()) {
                return new ListBoxModel().add("(credentials required)");
            }

            ApperianApi apperianApi = createApperianApi(upload);

            try {
                List<Application> apps = apperianApi.listApplications();
                ListBoxModel listItems = new ListBoxModel();
                for (Application app : apps) {
                    Version version = app.getVersion();
                    listItems.add(version.getAppName() + " v:" + version.getVersionNum() + " type:" + app.getTypeName(),
                            app.getId());
                }
                return listItems;
            } catch (ConnectionException e) {
                return new ListBoxModel().add("(" + e.getMessage() + ")");
            } catch (Exception e) {
                logger.throwing(EaseRecorder.class.getName(), "doFillAppItems", e);
                return new ListBoxModel().add("(error: " + e.getMessage() + ")");
            }
        }

        public ListBoxModel doFillCredentialItems(@QueryParameter("prodEnv") final String prodEnv,
                                                  @QueryParameter("customApperianUrl") String customApperianUrl,
                                                  @QueryParameter("apiTokenId") final String apiTokenId,
                                                  @QueryParameter("appId") final String appId) {
            EaseUpload upload = EaseUpload.simpleUpload(prodEnv, customApperianUrl, apiTokenId);

            if (!upload.validateHasAuthFields()) {
                return new ListBoxModel().add("(credentials required)");
            }

            boolean hasAppId = !Utils.isEmptyString(appId);

            ApperianApi apperianApi = createApperianApi(upload);

            try {

                PlatformType typeFilter = null;
                if (hasAppId) {
                    List<Application> apps = apperianApi.listApplications();

                    for (Application application : apps) {
                        if (appId.trim().equals(application.getId())) {
                            if (!application.isAppTypeSupportedByPlugin()) {
                                continue;
                            }
                            AppType appType = application.getAppType();
                            if (AppType.ANDROID.equals(appType)) {
                                typeFilter = PlatformType.ANDROID;
                            } else if (AppType.IOS.equals(appType)) {
                                typeFilter = PlatformType.IOS;
                            }
                            break;
                        }
                    }
                }


                List<SigningCredential> credentials = apperianApi.listCredentials();

                ListBoxModel listItems = new ListBoxModel();

                for (SigningCredential credential : credentials) {
                    if (typeFilter != null) {
                        if (!typeFilter.equals(credential.getPlatform())) {
                            continue;
                        }
                    }

                    listItems.add(credential.getDescription() +
                            " exp:" + Utils.transformDate(credential.getExpirationDate()) +
                            (typeFilter == null ? " platform:" + credential.getPlatform().getDisplayName() : ""),
                            credential.getCredentialId());
                }

                return listItems;
            } catch (ConnectionException e) {
                return new ListBoxModel().add("(" + e.getMessage() + ")");
            }

        }

        public FormValidation doTestConnection(@QueryParameter("prodEnv") final String prodEnv,
                                               @QueryParameter("customApperianUrl") String customApperianUrl,
                                               @QueryParameter("apiTokenId") final String apiTokenId)
                throws IOException, ServletException {
            EaseUpload upload = EaseUpload.simpleUpload(prodEnv, customApperianUrl, apiTokenId);

            if (!upload.validateHasAuthFields()) {
                return FormValidation.error("Api token and production environment should be provided");
            }

            ApperianApi apperianApi = createApperianApi(upload);

            try {
                // To check the connection we just use the endpoint to get the user details to see if it works
                apperianApi.getUserDetails();
                return FormValidation.ok("Connection OK");
            } catch (ConnectionException e) {
                return FormValidation.error(e.getMessage());
            }
        }

        private  ApperianApi createApperianApi(EaseUpload upload) {
            String environment = upload.prodEnv;
            String customApperianUrl = upload.customApperianUrl;
            String apiToken = credentialsManager.getCredentialWithId(upload.apiTokenId);
            return apperianApiFactory.create(environment, customApperianUrl, apiToken);
        }
    }
}
