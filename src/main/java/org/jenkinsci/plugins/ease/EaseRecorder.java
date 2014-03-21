package org.jenkinsci.plugins.ease;
import com.apperian.eas.*;
import hudson.Launcher;
import hudson.Extension;
import hudson.tasks.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
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
        File file = new File(filename.trim());
        if (!file.exists()) {
            logger.println("File not found: " + filename);
            return false;
        }

        PublishingEndpoint endpoint = new PublishingEndpoint(url);
        try {
            if (!uploadFile(logger, file, endpoint)) {
                return false;
            }
        } catch (IOException e) {
            logger.println("Connectivity problem");
            e.printStackTrace(logger);
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

        return true;
    }

    private boolean uploadFile(PrintStream logger, File file, PublishingEndpoint endpoint) throws IOException {
        logger.println("Publishing " + file + " to EASE");
        AuthenticateUserResponse auth = PublishingAPI.authenticateUser(username.trim(), password.trim())
                .call(endpoint);
        if (auth.hasError()) {
            String errorMessage = auth.getErrorMessage();
            logger.println(errorMessage);
            return true;
        }

        UpdateResponse update = PublishingAPI.update(auth.result.token, appId.trim())
                .call(endpoint);

        if (update.hasError()) {
            String errorMessage = auth.getErrorMessage();
            logger.println(errorMessage);
            return true;
        }

        UploadResult upload = endpoint.uploadFile(update.result.fileUploadURL, file);
        if (upload.hasError()) {
            String errorMessage = upload.errorMessage;
            logger.println(errorMessage);
            return true;
        }

        if (upload.fileID == null) {
            logger.println("Upload file ID is null. Publish transaction not finished");
            return true;
        }

        PublishResponse publish = PublishingAPI.publish(
                auth.result.token,
                update.result.transactionID,
                update.result.EASEmetadata,
                upload.fileID).call(endpoint);
        if (publish.hasError()) {
            String errorMessage = publish.getErrorMessage();
            logger.println(errorMessage);
            return true;
        }

        if (!appId.equals(publish.result.appID)) {
            logger.println("File uploaded but confirmational appId is wrong");
            return false;
        }

        logger.println("DONE! Uploaded " + file.getName() + " to " + url + " for appId=" + appId);
        return true;
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

