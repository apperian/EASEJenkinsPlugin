package org.jenkinsci.plugins.ease;

import com.apperian.eas.*;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PublishFileCallable implements FilePath.FileCallable<Boolean> {
    private final PrintStream logger;
    private final PublishingEndpoint endpoint;
    private String appId;
    private String username;
    private String password;
    private String url;

    public PublishFileCallable(PrintStream logger,
                               PublishingEndpoint endpoint,
                               String url,
                               String appId,
                               String username,
                               String password) {
        this.logger = logger;
        this.endpoint = endpoint;
        this.url = url;
        this.appId = appId;
        this.username = username;
        this.password = password;
    }

    public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        logger.println("Publishing " + f + " to EASE");
        AuthenticateUserResponse auth = PublishingAPI.authenticateUser(username, password)
                .call(endpoint);
        if (auth.hasError()) {
            String errorMessage = auth.getErrorMessage();
            logger.println(errorMessage);
            return false;
        }

        UpdateResponse update = PublishingAPI.update(auth.result.token, appId)
                .call(endpoint);

        if (update.hasError()) {
            String errorMessage = auth.getErrorMessage();
            logger.println(errorMessage);
            return false;
        }

        UploadResult upload = endpoint.uploadFile(update.result.fileUploadURL, f);
        if (upload.hasError()) {
            String errorMessage = upload.errorMessage;
            logger.println(errorMessage);
            return false;
        }

        if (upload.fileID == null) {
            logger.println("Upload file ID is null. Publish transaction not finished");
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
            logger.println("File uploaded but confirmational appId is wrong");
            return false;
        }

        logger.println("DONE! Uploaded " + f.getName() + " to " + url + " for appId=" + appId);
        return true;
    }
}
