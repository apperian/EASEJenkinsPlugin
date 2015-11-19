package com.apperian.eas.publishing;

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

    public GetListRequest getList(String token) {
        return new GetListRequest(token);
    }

    public UpdateRequest update(String token, String appID) {
        return new UpdateRequest(token, appID);
    }

    public PublishRequest publish(String token, String transactionID, Metadata metadata, String applicationFileId) {
        return new PublishRequest(token, transactionID, metadata, applicationFileId);
    }
}
