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

    private String prodEnv;
    private String customApperianUrl;
    private String appId;
    private String filename;
    private String apiTokenId;
    private String appName;
    private String shortDescription;
    private String longDescription;
    private String author;
    private String version;
    private String versionNotes;
    private boolean signApp;
    private String credential;
    private boolean enableApp;
    private boolean reapplyPolicies;

    private transient FilePath filePath;
    private transient Formatter<String> envVariablesFormatter = null;

    @DataBoundConstructor
    public EaseUpload(
            String prodEnv,
            String customApperianUrl,
            String apiTokenId,
            String appId,
            String filename,
            String appName,
            String shortDescription,
            String longDescription,
            String author,
            String version,
            String versionNotes,
            boolean signApp,
            String credential,
            boolean enableApp,
            boolean reapplyPolicies) {
        this.prodEnv = Utils.trim(prodEnv);
        this.customApperianUrl = Utils.trim(customApperianUrl);

        this.apiTokenId = Utils.trim(apiTokenId);
        this.appId = Utils.trim(appId);
        this.filename = Utils.trim(filename);

        //TODO:  Trim really necessary here?
        this.appName = Utils.trim(appName);
        this.shortDescription = Utils.trim(shortDescription);
        this.longDescription = Utils.trim(longDescription);

        this.author = Utils.trim(author);
        this.version = Utils.trim(version);
        this.versionNotes = Utils.trim(versionNotes);
        this.signApp = signApp;
        this.credential = credential;
        this.enableApp = enableApp;
        this.reapplyPolicies = reapplyPolicies;
    }

    public static class Builder {
        private EaseUpload easeUpload;

        public Builder(String prodEnv, String customApperianUrl, String apiTokenId) {
            easeUpload = new EaseUpload(prodEnv,
                customApperianUrl,
                apiTokenId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                false,
                false);
    }

        public Builder withAppId(String appId) {
            easeUpload.appId = appId;
            return this;
        }

        public Builder withFilename(String filename) {
            easeUpload.filename = filename;
            return this;
        }

        public Builder withAppName(String appName) {
            easeUpload.appName = appName;
            return this;
        }

        public Builder withShortDescription(String shortDescription) {
            easeUpload.shortDescription = shortDescription;
            return this;
        }

        public Builder withLongDescription(String longDescription) {
            easeUpload.longDescription = longDescription;
            return this;
        }

        public Builder withAuthor(String author) {
            easeUpload.author = author;
            return this;
        }

        public Builder withVersion(String version) {
            easeUpload.version = version;
            return this;
        }

        public Builder withVersionNotes(String versionNotes) {
            easeUpload.versionNotes = versionNotes;
            return this;
        }

        public Builder withEnableApp(boolean enableApp) {
            easeUpload.enableApp = enableApp;
            return this;
        }

        public Builder withReapplyPolicies(boolean reapplyPolicies) {
            easeUpload.reapplyPolicies = reapplyPolicies;
            return this;
        }

        public Builder withSignApp(boolean signApp) {
            easeUpload.signApp = signApp;
            return this;
        }

        public Builder withCredential(String credential) {
            easeUpload.credential = credential;
            return this;
        }

        public EaseUpload build() {
            return easeUpload;
        }

    }

    public String getProdEnv() {
        return prodEnv;
    }

    public String getCustomApperianUrl() {
        return customApperianUrl;
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

    public String getAppName() {
        return appName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
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

    public boolean getReapplyPolicies() { return reapplyPolicies; }

    public String getCredential() {
        return credential;
    }

    public boolean isEnableApp() {
        return enableApp;
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public String applyEnvVariablesFormatter(String value) {
        if (envVariablesFormatter != null) {
            value = envVariablesFormatter.format(value);
        }
        return value;
    }

    public void setEnvVariablesFormatter(Formatter<String> envVariablesFormatter) {
        this.envVariablesFormatter = envVariablesFormatter;
    }

    public void checkConfiguration() throws Exception{
        if (Utils.isEmptyString(appId)) {
            throw new Exception("The app id is empty");
        }

        checkHasAuthFields();

        if (Utils.isEmptyString(filename)){
            throw new Exception("The Filename is empty");
        }
    }

    public boolean searchFileInWorkspace(FilePath workspacePath,
                                   PrintStream buildLog) throws IOException, InterruptedException {
        FilePath[] paths = workspacePath.list(applyEnvVariablesFormatter(this.filename));
        if (paths.length != 1) {
            buildLog.println("Found " + (paths.length == 0 ? "no files" : " ambiguous list " + Arrays.asList(paths)) +
                    " as candidates for pattern '" + this.filename + "'");
            return false;
        }

        this.filePath = paths[0];
        return true;
    }

    public boolean validateHasAuthFields() {
        try {
            checkHasAuthFields();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void checkHasAuthFields() throws Exception{
        if (Utils.isEmptyString(apiTokenId)) {
            throw new Exception("Api Token is empty");
        }

        if (Utils.isEmptyString(this.prodEnv)) {
            throw new Exception("Production environment is empty");
        }

        ProductionEnvironment productionEnvironment = ProductionEnvironment.fromNameOrNA(this.prodEnv);
        if (productionEnvironment == null) {
            throw new Exception("Production environment is invalid");
        }

        if (productionEnvironment == ProductionEnvironment.CUSTOM) {
            if (!Utils.isValidURL(customApperianUrl)) {
                throw new Exception("API  URL is not a valid URL");
            }
        }
    }

    @Override
    public Descriptor<EaseUpload> getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<EaseUpload> {

        private static final transient Logger logger = Logger.getLogger(DescriptorImpl.class.getName());
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
            EaseUpload upload = new EaseUpload.Builder(prodEnv, customApperianUrl, apiTokenId).build();

            if (!upload.validateHasAuthFields()) {
                ListBoxModel listBoxModel = new ListBoxModel();
                listBoxModel.add("(credentials required)", "");
                return listBoxModel;
            }

            ApperianApi apperianApi = createApperianApi(upload);

            try {
                List<Application> apps = apperianApi.listApplications();
                ListBoxModel listItems = new ListBoxModel();
                for (Application app : apps) {
                    if (app.isAppTypeSupportedByPlugin()){
                        Version version = app.getVersion();
                        listItems.add(version.getAppName() + " v" + version.getVersionNum() + " type:" + app.getTypeName(),
                                app.getId());
                    }
                }
                return listItems;
            } catch (ConnectionException e) {
                ListBoxModel listBoxModel = new ListBoxModel();
                listBoxModel.add("(" + e.getMessage() + ")", "");
                return listBoxModel;
            } catch (Exception e) {
                logger.throwing(EaseRecorder.class.getName(), "doFillAppItems", e);
                ListBoxModel listBoxModel = new ListBoxModel();
                listBoxModel.add("(error: " + e.getMessage() + ")", "");
                return listBoxModel;
            }
        }

        public ListBoxModel doFillCredentialItems(@QueryParameter("prodEnv") final String prodEnv,
                                                  @QueryParameter("customApperianUrl") String customApperianUrl,
                                                  @QueryParameter("apiTokenId") final String apiTokenId,
                                                  @QueryParameter("appId") final String appId) {
            EaseUpload upload = new EaseUpload.Builder(prodEnv, customApperianUrl, apiTokenId).build();

            if (!upload.validateHasAuthFields()) {
                ListBoxModel listItems = new ListBoxModel();
                listItems.add("(credentials required)", "");
                return listItems;
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

                if (typeFilter != null) {
                    for (SigningCredential credential : credentials) {
                        if (!typeFilter.equals(credential.getPlatform())) {
                            continue;
                        }

                        listItems.add(credential.getDescription() +
                                " exp:" + Utils.transformDate(credential.getExpirationDate()),
                                credential.getCredentialId());
                    }
                }

                return listItems;
            } catch (ConnectionException e) {
                ListBoxModel listBoxModel = new ListBoxModel();
                listBoxModel.add("(" + e.getMessage() + ")", "");
                return listBoxModel;
            }

        }

        public FormValidation doTestConnection(@QueryParameter("prodEnv") final String prodEnv,
                                               @QueryParameter("customApperianUrl") String customApperianUrl,
                                               @QueryParameter("apiTokenId") final String apiTokenId)
                throws IOException, ServletException {
            EaseUpload upload = new EaseUpload.Builder(prodEnv, customApperianUrl, apiTokenId).build();

            try {
                upload.checkHasAuthFields();
            } catch (Exception e) {
                return FormValidation.error(e.getMessage());
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
