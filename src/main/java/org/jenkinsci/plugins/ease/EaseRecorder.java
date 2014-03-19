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
import java.io.IOException;

public class EaseRecorder extends Recorder {
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
//        private String easeAPIURL;
//        private String username;
//        private String password;

        public DescriptorImpl() {
            load();
        }

//        public FormValidation doCheckName(@QueryParameter String value)
//                throws IOException, ServletException {
//            if (value.length() == 0)
//                return FormValidation.error("Please set a name");
//            if (value.length() < 4)
//                return FormValidation.warning("Isn't the name too short?");
//            return FormValidation.ok();
//        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "EASE plugin";
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
            PublishingAPI api = new PublishingAPI(url);
            try {
                AuthenticateUserRequest request = new AuthenticateUserRequest(username, password);
                AuthenticateUserResponse response;
                response = request.call(api);

                PublishingResponse.JsonRpcError error = response.getError();
                if (error != null) {
                    if (error.checkError(APIConstants.ERROR_CODE_GENERIC)) {
                        return FormValidation.error(error.getDetailedMessage());
                    } else {
                        return FormValidation.error("JSON RPC error: " + error);
                    }
                }
                return FormValidation.ok("Success");
            } catch (IOException e) {
                return FormValidation.error("Connectivity problem: " + e.getMessage());
            } catch (Exception e) {
                return FormValidation.error("General problem: " + e.getMessage());
            }
        }

    }
}

