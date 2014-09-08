package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import com.apperian.eas.AuthenticateUserResponse;
import com.apperian.eas.PublishResponse;
import com.apperian.eas.PublishingAPI;
import com.apperian.eas.PublishingEndpoint;
import com.apperian.eas.UpdateResponse;
import com.apperian.eas.UploadResult;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

public class PublishFileCallable implements FilePath.FileCallable<Boolean>, Serializable {
    private final BuildListener listener;
    private final String appId;
    private final String username;
    private final String password;
    private final String url;

    public PublishFileCallable(EaseUpload upload, BuildListener listener) {
        this.listener = listener;

        this.url = upload.getUrl();
        this.username = upload.getUsername();
        this.password = upload.getPassword();
        this.appId = upload.getAppId();
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        PublishingEndpoint endpoint = new PublishingEndpoint(url);
        try {
            return publishFileToEndpoint(f, endpoint);
        } catch (Exception ex) {
            report("General plugin problem: " + ex);
            return false;
        } finally {
            endpoint.close();
        }
    }

    private Boolean publishFileToEndpoint(File f, PublishingEndpoint endpoint) throws IOException {
        report("Publishing " + f + " to EASE");

        EaseCredentials credentials = new EaseCredentials(url, username, password);
        credentials.lookupStoredCredentials();
        if (!credentials.checkOk()) {
            report("Error: username/password are not set and there is no stored credentials found");
            return false;
        }

        AuthenticateUserResponse auth = credentials.authenticate(endpoint);
        if (auth.hasError()) {
            String errorMessage = auth.getErrorMessage();
            report("Error: " + errorMessage + ", url=" + url);
            return false;
        }

        UpdateResponse update = PublishingAPI.update(auth.result.token, appId)
                .call(endpoint);

        if (update.hasError()) {
            String errorMessage = update.getErrorMessage();
            report("Error: " + errorMessage + ", appId=" + appId);
            return false;
        }

        UploadResult upload = endpoint.uploadFile(update.result.fileUploadURL, f);
        if (upload.hasError()) {
            String errorMessage = upload.errorMessage;
            report("Error: " + errorMessage);
            return false;
        }

        if (upload.fileID == null) {
            report("Error: Upload file ID is null. Publish transaction not finished");
            return false;
        }

        PublishResponse publish = PublishingAPI.publish(
                auth.result.token,
                update.result.transactionID,
                update.result.EASEmetadata,
                upload.fileID).call(endpoint);
        if (publish.hasError()) {
            String errorMessage = publish.getErrorMessage();
            report(errorMessage);
            return false;
        }

        if (!appId.equals(publish.result.appID)) {
            report("Error: File uploaded but confirmational appId is wrong");
            return false;
        }

        report("DONE! Uploaded " + f.getName() + " to " + url + " for appId=" + appId);
        return true;
    }

    public String getAppId() {
        return appId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public PrintStream getLogger() {
        return listener.getLogger();
    }

    private void report(String message) {
        getLogger().println(message);
    }

}
