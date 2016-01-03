package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;

import com.apperian.api.ApperianEaseApi;
import com.apperian.api.signing.ListAllSigningCredentialsResponse;
import com.apperian.api.signing.SigningCredential;
import hudson.FilePath;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.api.ApperianEaseEndpoint;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.apperian.api.publishing.ApplicationListResponse;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.Function1;
import net.sf.json.JSONObject;

import static com.apperian.api.publishing.ApplicationListResponse.Application;

public class EaseRecorder extends Recorder {
    public static final String PLUGIN_NAME = "Apperian EASE Plugin";

    private static final Logger logger = Logger.getLogger(EaseRecorder.class.getName());

    private final String url;
    private final String region;
    private final String customEaseUrl;
    private final String customApperianUrl;
    private final String username;
    private final String password;
    private final String appId;
    private final String filename;
    private final String author;
    private final String versionNotes;
    private final boolean sign;
    private final String credential;
    private final boolean enable;

    @DataBoundConstructor
    public EaseRecorder(
            String url,
            String region,
            String customEaseUrl,
            String customApperianUrl,
            String username,
            String password,
            String appId,
            String filename,
            String author,
            String versionNotes,
            boolean sign,
            String credential,
            boolean enable) {
        this.url = url;
        this.region = region;
        this.customEaseUrl = customEaseUrl;
        this.customApperianUrl = customApperianUrl;
        this.username = username;
        this.password = password;
        this.appId = appId;
        this.filename = filename;
        this.sign = sign;
        this.credential = credential;
        this.enable = enable;
        this.author = author;
        this.versionNotes = versionNotes;
    }

    public String getRegion() {
        if (region == null) {
            return Region.DEFAULT_REGION.name();
        }
        return region;
    }

    public String getCustomEaseUrl() {
        return customEaseUrl;
    }

    public String getCustomApperianUrl() {
        return customApperianUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAppId() {
        return appId;
    }

    public String getFilename() {
        return filename;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersionNotes() {
        return versionNotes;
    }

    public boolean isSign() {
        return sign;
    }

    public String getCredential() {
        return credential;
    }

    public boolean isEnable() {
        return enable;
    }

    @Override
    public boolean perform(final AbstractBuild build, Launcher launcher, final BuildListener listener) {
        final PrintStream buildLog = listener.getLogger();


        EaseUpload mainUpload = new EaseUpload(url, region, customEaseUrl,  customApperianUrl, username, password)
                .setOtherParams(appId, filename, author, versionNotes, sign, credential, enable);

        if (!mainUpload.checkOk()) {
            buildLog.println("One of required configuration options is not set");
            return false;
        }

        try {
            List<EaseUpload> allUploads = gatherAllUploads(mainUpload);

            Function1<String, String> expandVarFunctions;
            expandVarFunctions = new ExpandVariablesFunction(build, listener, buildLog);
            for (EaseUpload upload : allUploads) {
                upload.expand(expandVarFunctions);
            }

            for (Iterator<EaseUpload> iterator = allUploads.iterator(); iterator.hasNext(); ) {
                EaseUpload upload = iterator.next();
                if (!upload.checkOk()) {
                    buildLog.println("Additional upload skipped: '" + upload.getFilename() + "' -> appId='" + upload.getAppId() + "', specify appId, filename or url");
                    iterator.remove();
                }
            }

            boolean ok = true;
            for (EaseUpload upload : allUploads) {
                ok &= upload.searchWorkspace(build.getWorkspace(), buildLog);
            }
            if (!ok) {
                return false;
            }

            for (EaseUpload upload : allUploads) {
                FilePath path = upload.getFilePath();
                PublishFileCallable callable = new PublishFileCallable(upload, listener);

                if (!path.act(callable)) {
                    ok = false;
                }
            }
            return ok;
        } catch (IOException e) {
            buildLog.println("Connectivity or IO problem");
            e.printStackTrace(buildLog);
            return false;
        } catch (InterruptedException e) {
            buildLog.println("Execution stopped");
            return false;
        } catch (Exception e) {
            buildLog.println("General plugin problem");
            e.printStackTrace(buildLog);
            return false;
        }
    }

    private List<EaseUpload> gatherAllUploads(EaseUpload prototypeUpload) {
        List<EaseUpload> allUploads = new ArrayList<>();
        allUploads.add(prototypeUpload);
        logger.warning("Additional uploads was removed due the user story 'As a Jenkins plugin user, I would like to remove the ability to add additional uploads.'");
        return allUploads;
    }

    private static boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return PLUGIN_NAME;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }

        public ListBoxModel doFillRegionItems() {
            ListBoxModel resultListBox = new ListBoxModel();
            for (Region region : Region.values()) {
                if (region == Region.CUSTOM) {
                    continue;
                }
                resultListBox.add(region.getTitle(), region.name());
            }
            return resultListBox;
        }

        public ListBoxModel doFillAppIdItems(@QueryParameter("region") final String region,
                                             @QueryParameter("customApperianUrl") String customApperianUrl,
                                             @QueryParameter("customEaseUrl") String customEaseUrl,
                                             @QueryParameter("username") final String username,
                                             @QueryParameter("password") final String password) {
            EaseUpload upload = new EaseUpload(null,
                    region,
                    customEaseUrl,
                    customApperianUrl,
                    username,
                    password);

            if (!upload.checkHasFieldsForAuth()) {
                return new ListBoxModel().add("(no credentials)");
            }

            StringBuilder errorMessage = new StringBuilder();
            ApperianEaseEndpoint endpoint = upload.tryAuthenticate(true, false, errorMessage);
            if (endpoint == null) {
                return new ListBoxModel().add("(" + errorMessage + ")");
            }

            try {
                ApplicationListResponse response = ApperianEaseApi.PUBLISHING.list()
                        .call(endpoint.getEaseEndpoint());

                if (response.hasError()) {
                    return new ListBoxModel().add("(" + response.getErrorMessage() + ")");
                }

                Application[] apps = response.result.applications;
                ListBoxModel listItems = new ListBoxModel();
                for (Application app : apps) {
                    listItems.add(app.name + " v:" + app.version + " type:" + app.type,
                            app.ID);
                }
                return listItems;
            } catch (Exception e) {
                logger.throwing(EaseRecorder.class.getName(), "doFillAppItems", e);
                return new ListBoxModel().add("(error: " + e.getMessage() + ")");
            }
        }

        public ListBoxModel doFillCredentialItems(@QueryParameter("region") final String region,
                                                  @QueryParameter("customApperianUrl") String customApperianUrl,
                                                  @QueryParameter("customEaseUrl") String customEaseUrl,
                                                  @QueryParameter("username") final String username,
                                                  @QueryParameter("password") final String password) {
            EaseUpload upload = new EaseUpload(null,
                    region,
                    customEaseUrl,
                    customApperianUrl,
                    username,
                    password);

            if (!upload.checkHasFieldsForAuth()) {
                return new ListBoxModel().add("(no credentials)");
            }

            StringBuilder errorMessage = new StringBuilder();
            ApperianEaseEndpoint endpoint = upload.tryAuthenticate(false, true, errorMessage);
            if (endpoint == null) {
                return new ListBoxModel().add("(" + errorMessage + ")");
            }

            ListAllSigningCredentialsResponse response;
            try {
                response = ApperianEaseApi.SIGNING.listCredentials()
                        .call(endpoint.getApperianEndpoint());

                if (response.hasError()) {
                    return new ListBoxModel().add("(" + response + ")");
                }

                ListBoxModel listItems = new ListBoxModel();
                DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT);

                for (SigningCredential credential : response.getCredentials()) {
                    listItems.add(
                            credential.getDescription() + " exp:" + format.format(credential.getExpirationDate()) +
                                    " platform:" + credential.getPlatform().getDisplayName(),
                            credential.getCredentialId().getId());
                }

                return listItems;
            } catch (IOException e) {
                return new ListBoxModel().add("(no network)");
            }

        }


        public FormValidation doTestConnection(@QueryParameter("region") final String region,
                                               @QueryParameter("customApperianUrl") String customApperianUrl,
                                               @QueryParameter("customEaseUrl") String customEaseUrl,
                                               @QueryParameter("username") final String username,
                                               @QueryParameter("password") final String password)
                throws IOException, ServletException {

            EaseUpload upload = new EaseUpload(null,
                    region,
                    customEaseUrl,
                    customApperianUrl,
                    username,
                    password);


            if (!upload.checkHasFieldsForAuth()) {
                return FormValidation.error("Custom URLs should be non empty or region provided");
            }

            StringBuilder errorMessage = new StringBuilder();
            ApperianEaseEndpoint endpoint = upload.tryAuthenticate(true, true, errorMessage);
            if (endpoint == null) {
                return FormValidation.error(errorMessage.toString());
            }

            return FormValidation.ok("Connection OK");
        }

    }

    private static class ExpandVariablesFunction implements Function1<String, String> {
        private final AbstractBuild build;
        private final BuildListener listener;
        private final PrintStream logger;

        public ExpandVariablesFunction(AbstractBuild build, BuildListener listener, PrintStream logger) {
            this.build = build;
            this.listener = listener;
            this.logger = logger;
        }

        public String call(String value) {
            try {
                EnvVars environment = build.getEnvironment(listener);
                return environment.expand(value);
            } catch (IOException e) {
                logger.println("Environment expand error: " + e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return value;
        }
    }
}

