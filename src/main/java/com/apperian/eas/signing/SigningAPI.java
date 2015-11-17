package com.apperian.eas.signing;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Signing+API
 */
public class SigningAPI {
    public static ListAllSigningCredentialsRequest listAllSigningCredentials(String sessionToken) {
        return new ListAllSigningCredentialsRequest(sessionToken);
    }

    public static SignApplicationRequest signApplication(String sessionToken) {
        return new SignApplicationRequest(sessionToken);
    }
}
