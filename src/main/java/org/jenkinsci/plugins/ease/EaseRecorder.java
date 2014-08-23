package org.jenkinsci.plugins.ease;

import com.apperian.eas.*;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
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
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.apperian.eas.GetListResponse.Application;

public class EaseRecorder extends Recorder {
    public static final String PLUGIN_NAME = "EASE Plugin";

    private static final Logger logger = Logger.getLogger(EaseRecorder.class.getName());

    private final EaseUpload[] additionalUploads;
    private final String url;
    private final String username;
    private final String password;
    private final String appId;
    private final String filename;

    @DataBoundConstructor
    public EaseRecorder(String url, String username, String password, String appId, String filename, EaseUpload[] additionalUploads) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.appId = appId;
        this.filename = filename;
        this.additionalUploads = additionalUploads;
    }

    public String getUrl() {
        return url;
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

    public EaseUpload[] getAdditionalUploads() { return additionalUploads; }

    @Override
    public boolean perform(final AbstractBuild build, Launcher launcher, final BuildListener listener) {
        final PrintStream logger = listener.getLogger();

        EaseUpload mainUpload = new EaseUpload(url, username, password, appId, filename);
        if (!mainUpload.checkOk()) {
            logger.println("One of required configuration options is not set");
            return false;
        }

        try {
            List<EaseUpload> allUploads = gatherAllUploads(mainUpload);

            Function1<String, String> expandVarFunctions;
            expandVarFunctions = new ExpandVariablesFunction(build, listener, logger);
            for (EaseUpload upload : allUploads) {
                upload.expand(expandVarFunctions);
            }

            for (Iterator<EaseUpload> iterator = allUploads.iterator(); iterator.hasNext(); ) {
                EaseUpload upload = iterator.next();
                if (!upload.checkOk()) {
                    logger.println("Additional upload skipped: '" + upload.getFilename() + "' -> " + upload.getAppId());
                    iterator.remove();
                }
            }

            boolean error = false;
            for (EaseUpload upload : allUploads) {
                String filename = upload.getFilename();

                FilePath[] paths = build.getWorkspace().list(filename);
                if (paths.length != 1) {
                    logger.println("Found " + (paths.length == 0 ? "no files" : " ambiguous list " + Arrays.asList(paths)) +
                            " as candidates for pattern '" + filename + "'");
                    error = true;
                    continue;
                }

                upload.setFilePath(paths[0]);
            }

            if (error) {
                return false;
            }

            for (EaseUpload upload : allUploads) {
                if (!upload.checkOk()) {
                    continue;
                }

                if (!upload.getFilePath().act(
                        new PublishFileCallable(logger, upload))) {
                    error = true;
                }
            }
            return !error;
        } catch (IOException e) {
            logger.println("Connectivity or IO problem");
            e.printStackTrace(logger);
            return false;
        } catch (InterruptedException e) {
            logger.println("Execution stopped");
            return false;
        } catch (Exception e) {
            logger.println("General plugin problem");
            e.printStackTrace(logger);
            return false;
        }
    }

    private List<EaseUpload> gatherAllUploads(EaseUpload prototypeUpload) {
        List<EaseUpload> allUploads = new ArrayList<EaseUpload>();
        allUploads.add(prototypeUpload);
        if (additionalUploads != null) {
            for (EaseUpload additionalUpload : additionalUploads) {
                allUploads.add(prototypeUpload.derive(additionalUpload));
            }
        }
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

        public FormValidation doTestConnection(@QueryParameter("url") final String url,
                                               @QueryParameter("username") final String username,
                                               @QueryParameter("password") final String password)
                throws IOException, ServletException {
            if (isEmptyString(url)) {
                return FormValidation.error("API URL should not be empty");
            }

            if (url.contains("___")) {
                return FormValidation.error("Fix URL to 'https://easesvc.apperian.com/ease.interface.php' or 'https://easesvc.apperian.eu/ease.interface.php'");
            }

            try {
                return checkConnectivity(url, username, password);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Connectivity problem", e);
                return FormValidation.error("Connectivity problem: " + e.getMessage());
            } catch (Exception e) {
                logger.log(Level.WARNING, "General plugin problem", e);
                return FormValidation.error("General plugin problem: " + e.getMessage());
            }
        }

        private FormValidation checkConnectivity(String url, String username, String password) throws IOException {
            PublishingEndpoint endpoint = new PublishingEndpoint(url);

            EaseCredentials credentials = new EaseCredentials(url, username, password);
            credentials.lookupStoredCredentials();

            if (!credentials.checkOk()) {
                return FormValidation.error("Username/password are not set and there is no stored credentials found");
            }


            AuthenticateUserResponse authResponse = credentials.authenticate(endpoint);
            if (authResponse.hasError()) {
                String errorMessage = authResponse.getErrorMessage();
                return FormValidation.error(errorMessage);
            }

            GetListResponse getListResponse = PublishingAPI.getList(authResponse.result.token)
                    .call(endpoint);

            if (getListResponse.hasError()) {
                String errorMessage = authResponse.getErrorMessage();
                return FormValidation.error(errorMessage);
            }

            List<Application> list = new ArrayList<Application>(
                    Arrays.asList(getListResponse.result.applications));
            Collections.sort(list, new Comparator<Application>() {
                public int compare(Application o1, Application o2) {
                    return o1.name.compareTo(o2.name);
                }
            });

            StringBuilder sb = new StringBuilder();
            for (Application app : list) {
                sb.append("'").append(app.name).append("'->").append(app.ID).append(' ');
            }

            return FormValidation.ok("Connection OK! AppIds: " + sb);
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

