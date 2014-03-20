package com.apperian.eas;

public class PublishingAPI {
    public static AuthenticateUserRequest authenticateUser(String username, String password) {
        return new AuthenticateUserRequest(username, password);
    }

    public static GetListRequest getList(String token) {
        return new GetListRequest(token);
    }
}
