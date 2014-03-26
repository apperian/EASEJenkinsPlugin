package org.jenkinsci.plugins.ease;

import com.apperian.eas.*;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PublishFileCallable implements FilePath.FileCallable<Boolean> {
    private final PrintStream logger;
    private String appId;
    private String username;
    private String password;
    private String url;

    public PublishFileCallable(PrintStream logger,
                               EaseUpload upload) {
        this.logger = logger;

        this.url = upload.getUrl();
        this.username = upload.getUsername();
        this.password = upload.getPassword();
        this.appId = upload.getAppId();
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        PublishingEndpoint endpoint = new PublishingEndpoint(url);
        try {
            logger.println("Publishing " + f + " to EASE");

            EaseCredentials credentials = new EaseCredentials(url, username, password);
            credentials.lookupStoredCredentials();
            if (!credentials.checkOk()) {
                logger.println("Error: username/password are not set and there is no stored credentials found");
                return false;
            }

            AuthenticateUserResponse auth = credentials.authenticate(endpoint);
            if (auth.hasError()) {
                String errorMessage = auth.getErrorMessage();
                logger.println("Error: " + errorMessage + ", url=" + url);
                return false;
            }

            UpdateResponse update = PublishingAPI.update(auth.result.token, appId)
                    .call(endpoint);

            if (update.hasError()) {
                String errorMessage = update.getErrorMessage();
                logger.println("Error: " + errorMessage + ", appId=" + appId);
                return false;
            }

            UploadResult upload = endpoint.uploadFile(update.result.fileUploadURL, f);
            if (upload.hasError()) {
                String errorMessage = upload.errorMessage;
                logger.println("Error: " + errorMessage);
                return false;
            }

            if (upload.fileID == null) {
                logger.println("Error: Upload file ID is null. Publish transaction not finished");
                return false;
            }

            PublishResponse publish = PublishingAPI.publish(
                    auth.result.token,
                    update.result.transactionID,
                    update.result.EASEmetadata,
                    upload.fileID).call(endpoint);
            if (publish.hasError()) {
                String errorMessage = publish.getErrorMessage();
                logger.println(errorMessage);
                return false;
            }

            if (!appId.equals(publish.result.appID)) {
                logger.println("Error: File uploaded but confirmational appId is wrong");
                return false;
            }

            logger.println("DONE! Uploaded " + f.getName() + " to " + url + " for appId=" + appId);
            return true;
        } catch (Exception ex) {
            logger.println("General plugin problem: " + ex);
            return false;
        } finally {
            endpoint.close();
        }
    }
}
