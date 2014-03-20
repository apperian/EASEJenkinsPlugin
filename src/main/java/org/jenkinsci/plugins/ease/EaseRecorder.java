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
import org.kohsuke.stapler.bind.JavaScriptMethod;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EaseRecorder extends Recorder {
    public static final String PLUGIN_NAME = "EASE publishing plugin";

    private static final Logger logger = Logger.getLogger(EaseRecorder.class.getName());
    private String url;
    private String username;
    private String password;

    @DataBoundConstructor
    public EaseRecorder(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
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

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        return true;
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
            if (url == null || url.trim().isEmpty()) {
                return FormValidation.error("API URL should not be empty");
            }
            PublishingEndpoint api = new PublishingEndpoint(url);
            try {
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

                return FormValidation.ok("Connection OK! " + getListResponse);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Connectivity problem", e);
                return FormValidation.error("Connectivity problem: " + e.getMessage());
            } catch (Exception e) {
                logger.log(Level.WARNING, "General problem", e);
                return FormValidation.error("General problem: " + e.getMessage());
            }
        }

    }
}

