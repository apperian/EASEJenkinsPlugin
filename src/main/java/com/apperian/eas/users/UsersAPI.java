package com.apperian.eas.users;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Users+API
 */
public class UsersAPI {
    public static AuthenticateUserRequest authenticateUser(String userId, String password) {
        return new AuthenticateUserRequest(userId, password);
    }
}
