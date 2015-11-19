package com.apperian.api.publishing;

/**
 * API described at:
 * https://help.apperian.com/display/pub/EASE+Publishing+API+Guide
 */
public class Publishing {
    public static Publishing API = new Publishing();

    Publishing() {
    }

    public AuthenticateUserRequest authenticateUser(String username, String password) {
        return new AuthenticateUserRequest(username, password);
    }

    public GetListRequest getList() {
        return new GetListRequest();
    }

    public UpdateRequest update(String appID) {
        return new UpdateRequest(appID);
    }

    public PublishRequest publish(String transactionID, Metadata metadata, String applicationFileId) {
        return new PublishRequest(transactionID, metadata, applicationFileId);
    }
}
