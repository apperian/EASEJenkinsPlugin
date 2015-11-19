package com.apperian.eas.signing;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Signing+API
 */
public class Signing {
    public static Signing API = new Signing();

    Signing() {
    }

    public ListAllSigningCredentialsRequest listAllSigningCredentials(String sessionToken) {
        return new ListAllSigningCredentialsRequest(sessionToken);
    }

    public SignApplicationRequest signApplication(String sessionToken) {
        return new SignApplicationRequest(sessionToken);
    }
}
