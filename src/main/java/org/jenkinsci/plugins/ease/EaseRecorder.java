package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

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
import net.sf.json.JSONObject;

public class EaseRecorder extends Recorder {
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
    public boolean perform(final AbstractBuild build, Launcher launcher, final BuildListener listener) {
        final PrintStream buildLog = listener.getLogger();
        final EnvVars environment;
        try {
            environment = build.getEnvironment(listener);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error getting Jenkins environment", e);
        }

        if (uploads == null || uploads.isEmpty()) {
            buildLog.println("One of required configuration options is not set");
            return false;
        }

        try {
            for (EaseUpload upload : uploads) {
                if (!upload.isConfigurationValid()) {
                    buildLog.println("Additional upload skipped: '" + upload.getFilename() +
                                     "' -> appId='" + upload.getAppId() + "', invalid configuration");
                    return false;
                }

                Formatter<String> envVariablesFormatter = new Formatter<String>() {
                    public String format(String value) {
                        return environment.expand(value);
                    }
                };
                upload.setEnvVariablesFormatter(envVariablesFormatter);

                if (!upload.searchFileInWorkspace(build.getWorkspace(), buildLog)) {
                    return false;
                }

                FilePath path = upload.getFilePath();
                PublishFileCallable callable = new PublishFileCallable(upload, listener);
                if (!path.act(callable)) {
                    return false;
                }
            }
            return true;
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
        return (DescriptorImpl) super.getDescriptor();
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
            return super.configure(req, formData);
        }

    }

}

