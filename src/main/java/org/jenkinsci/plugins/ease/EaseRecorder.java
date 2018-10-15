package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;

public class EaseRecorder extends Recorder implements SimpleBuildStep {
    public static final String PLUGIN_NAME = "Apperian Plugin";

    private final List<EaseUpload> uploads;

    @DataBoundConstructor
    public EaseRecorder(List<EaseUpload> uploads) {
        this.uploads = uploads;
    }

    public List<EaseUpload> getUploads() {
        return uploads;
    }


    @Override
    public void perform(final Run build, FilePath workspace, Launcher launcher, final TaskListener listener) {
        final PrintStream buildLog = listener.getLogger();
        final EnvVars environment;
        try {
            environment = build.getEnvironment(listener);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error getting Jenkins environment", e);
        }

        if (uploads == null || uploads.isEmpty()) {
            buildLog.println("One of required configuration options is not set");
            throw new RuntimeException("One of required configuration options is not set");
        }

        try {
            for (EaseUpload upload : uploads) {
                try {
                    upload.checkConfiguration();
                } catch (Exception e) {
                    buildLog.println("Invalid configuration. " + e.getMessage());
                    throw new RuntimeException("Invalid configuration. " + e.getMessage(), e);
                }

                Formatter<String> envVariablesFormatter = new Formatter<String>() {
                    public String format(String value) {
                        return environment.expand(value);
                    }
                };
                upload.setEnvVariablesFormatter(envVariablesFormatter);

                if (!upload.searchFileInWorkspace(workspace, buildLog)) {
                    throw new RuntimeException("Could not find file in workspace");
                }

                FilePath path = upload.getFilePath();
                PublishFileCallable callable = new PublishFileCallable(upload, listener);
                if (!path.act(callable)) {
                    //TODO:  Update this message
                    throw new RuntimeException("Could not execute PublishFileCallable");
                }
            }
        } catch (IOException e) {
            buildLog.println("Connectivity or IO problem");
            e.printStackTrace(buildLog);
            throw new RuntimeException("Connectivity or IO problem", e);
        } catch (InterruptedException e) {
            buildLog.println("Execution stopped");
            throw new RuntimeException("Execution stopped", e);
        } catch (Exception e) {
            buildLog.println("General plugin problem");
            e.printStackTrace(buildLog);
            throw new RuntimeException("General plugin problem", e);
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher>  {
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
            return super.configure(req, formData);
        }

    }

}

