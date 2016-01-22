package org.jenkinsci.plugins.ease;

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
import hudson.util.Function1;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class EaseRecorder extends Recorder {
    public static final String PLUGIN_NAME = "Apperian EASE Plugin";

    private static final Logger logger = Logger.getLogger(EaseRecorder.class.getName());

    private final List<EaseUpload> uploads;

    @DataBoundConstructor
    public EaseRecorder(List<EaseUpload> uploads) {
        this.uploads = uploads;
    }

    public List<EaseUpload> getUploads() { return uploads; }

    @Override
    public boolean perform(final AbstractBuild build, Launcher launcher, final BuildListener listener) {
        final PrintStream buildLog = listener.getLogger();


        if (uploads == null || uploads.isEmpty()) {
            buildLog.println("One of required configuration options is not set");
            return false;
        }

        try {
            Function1<String, String> expandVarFunctions;
            expandVarFunctions = new ExpandVariablesFunction(build, listener, buildLog);
            for (EaseUpload upload : uploads) {
                upload.expand(expandVarFunctions);
            }

            for (Iterator<EaseUpload> iterator = uploads.iterator(); iterator.hasNext(); ) {
                EaseUpload upload = iterator.next();
                if (!upload.checkOk()) {
                    buildLog.println("Additional upload skipped: '" + upload.getFilename() + "' -> appId='" + upload.getAppId() + "', specify appId, filename or url");
                    iterator.remove();
                }
            }

            boolean ok = true;
            for (EaseUpload upload : uploads) {
                ok &= upload.searchWorkspace(build.getWorkspace(), buildLog);
            }
            if (!ok) {
                return false;
            }

            for (EaseUpload upload : uploads) {
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

