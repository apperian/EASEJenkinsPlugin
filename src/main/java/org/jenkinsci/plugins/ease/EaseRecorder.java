package org.jenkinsci.plugins.ease;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
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

        if (uploads == null || uploads.isEmpty()) {
            buildLog.println("One of required configuration options is not set");
            return false;
        }

        try {
            // TODO:  Consolidate these 'for' loops.
            for (EaseUpload upload : uploads) {
                expandUploadArgs(upload, build, listener, buildLog);
            }

            for (Iterator<EaseUpload> iterator = uploads.iterator(); iterator.hasNext(); ) {
                EaseUpload upload = iterator.next();
                if (!upload.isConfigurationValid()) {
                    buildLog.println("Additional upload skipped: '" + upload.filename + "' -> appId='" + upload.appId + "', specify appId, filename or url");
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
                FilePath path = upload.filePath;
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

    // TODO:  What does 'expand' do to a variable?  And why do we need it?
    private void expandUploadArgs(EaseUpload upload, AbstractBuild build, BuildListener listener, PrintStream logger) {
        try {
            EnvVars environment = build.getEnvironment(listener);
            upload.appId = environment.expand(upload.appId);
            upload.filename = environment.expand(upload.filename);
            upload.author = environment.expand(upload.author);
            upload.version = environment.expand(upload.version);
            upload.versionNotes = environment.expand(upload.versionNotes);
        } catch (IOException e) {
            logger.println("Environment expand error: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

