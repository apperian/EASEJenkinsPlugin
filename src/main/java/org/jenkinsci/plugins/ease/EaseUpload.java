package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import org.jenkinsci.plugins.api.ApperianEaseEndpoint;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.ApperianEndpoint;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.publishing.ApplicationListResponse;
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
import hudson.util.Secret;

public class EaseUpload implements Describable<EaseUpload>, Serializable, Cloneable {
    private static final Logger logger = Logger.getLogger(EaseUpload.class.getName());

    private final String prodEnv;
    private final String customEaseUrl;
    private final String customApperianUrl;
    private final String appId;
    private final String filename;
    private final String apiTokenId;
    private final String author;
    private final String version;
    private final String versionNotes;
    private final boolean signApp;
    private final String credential;
    private final boolean enableApp;

    private FilePath filePath;

    @DataBoundConstructor
    public EaseUpload(
            String prodEnv,
            String customEaseUrl,
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
        this.customEaseUrl = Utils.trim(customEaseUrl);
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
            @QueryParameter("customEaseUrl") String customEaseUrl,
            @QueryParameter("apiTokenId") String apiTokenId) {

        return new EaseUpload(prodEnv,
                customEaseUrl,
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

    public EaseUpload expand(Function1<String, String> expandVars) {
        return new EaseUpload(prodEnv,
                              customEaseUrl,
                              customApperianUrl,
                              apiTokenId,
                              expandVars.call(appId),
                              expandVars.call(filename),
                              expandVars.call(author),
                              expandVars.call(version),
                              expandVars.call(versionNotes),
                              signApp,
                              credential,
                              enableApp);
    }

    public String getProdEnv() {
        return prodEnv;
    }

    public String getCustomEaseUrl() {
        return customEaseUrl;
    }

    public String getCustomApperianUrl() {
        return customApperianUrl;
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public String getAppId() {
        return appId;
    }

    public String getFilename() {
        return filename;
    }

    public String getApiTokenId() {
        return apiTokenId;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionNotes() {
        return versionNotes;
    }

    public boolean isSignApp() {
        return signApp;
    }

    public String getCredential() {
        return credential;
    }

    public boolean isEnableApp() {
        return enableApp;
    }

    public boolean checkOk() {
        return !Utils.isEmptyString(appId) &&
                validateHasAuthFields() &&
                !Utils.isEmptyString(filename);
    }



    public boolean searchWorkspace(FilePath workspacePath,
                                   PrintStream buildLog) throws IOException, InterruptedException {
        String filename = getFilename();
        FilePath[] paths = workspacePath.list(filename);
        if (paths.length != 1) {
            buildLog.println("Found " + (paths.length == 0 ? "no files" : " ambiguous list " + Arrays.asList(paths)) +
                    " as candidates for pattern '" + filename + "'");
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
            if (!Utils.isValidURL(customEaseUrl)) {
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

        private transient APIManager apiManager = new APIManager();

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
            List<EaseUser> credentials = credentialsManager.getCredentials();
            resultListBox.add("<Select an API token>", "");
            for (EaseUser easeUser : credentials) {
                resultListBox.add(easeUser.getDescription(), easeUser.getApiTokenId());
            }
            return resultListBox;
        }

        public ListBoxModel doFillAppIdItems(@QueryParameter("prodEnv") final String prodEnv,
                                             @QueryParameter("customApperianUrl") String customApperianUrl,
                                             @QueryParameter("customEaseUrl") String customEaseUrl,
                                             @QueryParameter("apiTokenId") final String apiTokenId) {
            EaseUpload upload = EaseUpload.simpleUpload(prodEnv, customApperianUrl, customEaseUrl, apiTokenId);

            if (!upload.validateHasAuthFields()) {
                return new ListBoxModel().add("(credentials required)");
            }

            StringBuilder errorMessage = new StringBuilder();
            ApperianEaseEndpoint endpoint = apiManager.createConnection(upload, true, false, errorMessage);
            if (endpoint == null) {
                return new ListBoxModel().add("(" + errorMessage + ")");
            }

            try {
                ApplicationListResponse response = ApperianEaseApi.PUBLISHING.list()
                        .call(endpoint.getEaseEndpoint());

                if (response.hasError()) {
                    return new ListBoxModel().add("(" + response.getErrorMessage() + ")");
                }

                ApplicationListResponse.Application[] apps = response.result.applications;
                ListBoxModel listItems = new ListBoxModel();
                for (ApplicationListResponse.Application app : apps) {
                    listItems.add(app.name + " v:" + app.version + " type:" + app.type,
                            app.ID);
                }
                return listItems;
            } catch (Exception e) {
                logger.throwing(EaseRecorder.class.getName(), "doFillAppItems", e);
                return new ListBoxModel().add("(error: " + e.getMessage() + ")");
            }
        }

        public ListBoxModel doFillCredentialItems(@QueryParameter("prodEnv") final String prodEnv,
                                                  @QueryParameter("customApperianUrl") String customApperianUrl,
                                                  @QueryParameter("customEaseUrl") String customEaseUrl,
                                                  @QueryParameter("apiTokenId") final String apiTokenId,
                                                  @QueryParameter("appId") final String appId) {
            EaseUpload upload = EaseUpload.simpleUpload(prodEnv, customApperianUrl, customEaseUrl, apiTokenId);

            if (!upload.validateHasAuthFields()) {
                return new ListBoxModel().add("(credentials required)");
            }

            boolean hasAppId = !Utils.isEmptyString(appId);

            StringBuilder errorMessage = new StringBuilder();
            ApperianEaseEndpoint endpoint = apiManager.createConnection(upload, hasAppId, true, errorMessage);
            if (endpoint == null) {
                return new ListBoxModel().add("(" + errorMessage + ")");
            }

            try {
                PlatformType typeFilter = null;
                if (hasAppId) {
                    ApplicationListResponse response = ApperianEaseApi.PUBLISHING.list()
                            .call(endpoint.getEaseEndpoint());

                    if (!response.hasError()) {
                        for (ApplicationListResponse.Application application : response.result.applications) {
                            if (appId.trim().equals(application.ID)) {
                                if (Utils.isEmptyString(application.type)) {
                                    continue;
                                }
                                if (application.type.contains("Android App")) {
                                    typeFilter = PlatformType.ANDROID;
                                } else if (application.type.contains("iOS App")) {
                                    typeFilter = PlatformType.IOS;
                                }
                                break;
                            }
                        }
                    }
                }


                ListAllSigningCredentialsResponse response;
                response = ApperianEaseApi.SIGNING.listCredentials()
                        .call(endpoint.getApperianEndpoint());

                if (response.hasError()) {
                    return new ListBoxModel().add("(" + response + ")");
                }

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
            } catch (IOException e) {
                return new ListBoxModel().add("(network required)");
            }

        }

        public FormValidation doTestConnection(@QueryParameter("prodEnv") final String prodEnv,
                                               @QueryParameter("customApperianUrl") String customApperianUrl,
                                               @QueryParameter("customEaseUrl") String customEaseUrl,
                                               @QueryParameter("apiTokenId") final String apiTokenId)
                throws IOException, ServletException {
            EaseUpload upload = EaseUpload.simpleUpload(prodEnv, customApperianUrl, customEaseUrl, apiTokenId);


            if (!upload.validateHasAuthFields()) {
                return FormValidation.error("Api token and production environment should be provided");
            }

            StringBuilder errorMessage = new StringBuilder();
            ApperianEaseEndpoint endpoint = apiManager.createConnection(upload, true, true, errorMessage);
            if (endpoint == null) {
                return FormValidation.error(errorMessage.toString());
            }

            return FormValidation.ok("Connection OK");
        }
    }
}
