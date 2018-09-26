package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import org.jenkinsci.plugins.api.ApiConnection;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ConnectionException;
import com.apperian.api.application.AppType;
import com.apperian.api.application.Application;
import com.apperian.api.application.ApplicationListResponse;
import com.apperian.api.application.Application.Version;
import com.apperian.api.signing.ListAllSigningCredentialsResponse;
import com.apperian.api.signing.PlatformType;
import com.apperian.api.signing.SigningCredential;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.Function1;
import hudson.util.ListBoxModel;

public class EaseUpload implements Describable<EaseUpload>, Serializable, Cloneable {
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

    public boolean checkOk() {
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

        private transient ApiManager apiManager = new ApiManager();
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

            ApiConnection apiConnection = createConnection(upload);

            try {
                ApperianEndpoint apperianEndpoint = apiConnection.getApperianEndpoint();
                ApplicationListResponse response = ApperianEaseApi.APPLICATIONS.list(apperianEndpoint);

                List<Application> apps = response.getApplications();
                ListBoxModel listItems = new ListBoxModel();
                for (Application app : apps) {
                    Version version = app.getVersion();
                    listItems.add(version.getAppName() + " v:" + version.getVersionNum() + " type:" + app.getTypeName(),
                            app.getId().getId());
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

            ApiConnection apiConnection = createConnection(upload);

            try {

                PlatformType typeFilter = null;
                if (hasAppId) {
                    ApperianEndpoint apperianEndpoint = apiConnection.getApperianEndpoint();
                    ApplicationListResponse response = ApperianEaseApi.APPLICATIONS.list(apperianEndpoint);

                    for (Application application : response.getApplications()) {
                        if (appId.trim().equals(application.getId().getId())) {
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


                ListAllSigningCredentialsResponse response;
                response = ApperianEaseApi.SIGNING.listCredentials()
                        .call(apiConnection.getApperianEndpoint());

                ListBoxModel listItems = new ListBoxModel();
                DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT);

                for (SigningCredential credential : response.getCredentials()) {
                    if (typeFilter != null) {
                        if (!typeFilter.equals(credential.getPlatform())) {
                            continue;
                        }
                    }

                    listItems.add(credential.getDescription() +
                            " exp:" + Utils.transformDate(credential.getExpirationDate()) +
                            (typeFilter == null ? " platform:" + credential.getPlatform().getDisplayName() : ""),

                            credential.getCredentialId().getId());
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

            ApiConnection apiConnection = createConnection(upload);

            try {
                ApperianEndpoint apperianEndpoint = apiConnection.getApperianEndpoint();
                if (!apiManager.isConnectionSuccessful(apperianEndpoint)) {
                    throw new ConnectionException("The connection with Apperian is not correct");
                }

                return FormValidation.ok("Connection OK");
            } catch (ConnectionException e) {
                return FormValidation.error(e.getMessage());
            }
        }

        private ApiConnection createConnection(EaseUpload upload) {
            String environment = upload.prodEnv;
            String customApperianUrl = upload.customApperianUrl;
            String apiToken = credentialsManager.getCredentialWithId(upload.apiTokenId);
            return apiManager.createConnection(environment, customApperianUrl, apiToken);
        }
    }
}
