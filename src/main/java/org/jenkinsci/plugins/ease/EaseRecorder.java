package org.jenkinsci.plugins.ease;

import com.apperian.eas.*;
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
    public static final String PLUGIN_NAME = "EASE publishing plugin";

    private static final Logger logger = Logger.getLogger(EaseRecorder.class.getName());
    private String url;
    private String username;
    private String password;
    private String appId;
    private String filename;

    @DataBoundConstructor
    public EaseRecorder(String url, String username, String password, String appId, String filename) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.appId = appId;
        this.filename = filename;
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

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        PrintStream logger = listener.getLogger();

        if (isEmptyString(appId)
                || isEmptyString(url)
                || isEmptyString(username)
                || isEmptyString(password)
                || isEmptyString(filename)) {
            logger.println("One of configuration options is not filled");
            return false;
        }

        PublishingEndpoint endpoint = new PublishingEndpoint(url);
        try {

            String pattern = filename.trim();
            FilePath[] paths = build.getWorkspace().list(pattern);
            if (paths.length != 1) {
                logger.println("Found " + (paths.length == 0 ? "no files" : " ambiguous list " + Arrays.asList(paths)) +
                        " as candidates for '" + pattern + "'");
                return false;
            }

            FilePath file = paths[0];

            return file.act(
                    new PublishFileCallable(logger, endpoint,
                        url.trim(),
                        appId.trim(),
                        username.trim(),
                        password.trim()));

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
        } finally {
            try {
                endpoint.close();
            } catch (IOException e) {
                logger.println("Connectivity problem");
                e.printStackTrace(logger);
            }
        }
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
//            easeAPIURL = formData.getString("easeAPIURL");
//            username = formData.getString("username");
//            password = formData.getString("password");
            save();
            return super.configure(req,formData);
        }

        public FormValidation doTestConnection(@QueryParameter("url") final String url,
                                               @QueryParameter("username") final String username,
                                               @QueryParameter("password") final String password)
                throws IOException, ServletException {
            if (isEmptyString(url)
                    || isEmptyString(username)
                    || isEmptyString(password)) {
                return FormValidation.error("API URL, username and password should not be empty");
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
            PublishingEndpoint api = new PublishingEndpoint(url);

            AuthenticateUserResponse authResponse = PublishingAPI.authenticateUser(username, password)
                    .call(api);

            if (authResponse.hasError()) {
                String errorMessage = authResponse.getErrorMessage();
                return FormValidation.error(errorMessage);
            }

            GetListResponse getListResponse = PublishingAPI.getList(authResponse.result.token)
                    .call(api);

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

}

