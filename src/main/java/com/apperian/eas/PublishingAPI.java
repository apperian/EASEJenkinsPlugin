package com.apperian.eas;

public class PublishingAPI {
    public static AuthenticateUserRequest authenticateUser(String username, String password) {
        return new AuthenticateUserRequest(username, password);
    }

    public static GetListRequest getList(String token) {
        return new GetListRequest(token);
    }

    public static UpdateRequest update(String token, String appID) {
        return new UpdateRequest(token, appID);
    }

    public static PublishRequest publish(String token, String transactionID, Metadata metadata, String applicationFileId) {
        return new PublishRequest(token, transactionID, metadata, applicationFileId);
    }
}
