package com.apperian.api.publishing;

import com.apperian.api.metadata.Metadata;

/**
 * API described at:
 * https://help.apperian.com/display/pub/EASE+Publishing+API+Guide
 */
public class Publishing {
    public Publishing() {
    }

    public AuthenticateUserRequest authenticateUser(String username, String password) {
        return new AuthenticateUserRequest(username, password);
    }

    public ApplicationListRequest list() {
        return new ApplicationListRequest();
    }

    public UpdateApplicationRequest update(String appID) {
        return new UpdateApplicationRequest(appID);
    }

    public PublishApplicationRequest publish(String transactionID, Metadata metadata, String applicationFileId) {
        return new PublishApplicationRequest(transactionID, metadata, applicationFileId);
    }
}
