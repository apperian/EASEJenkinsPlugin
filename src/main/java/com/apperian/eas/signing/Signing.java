package com.apperian.eas.signing;

import com.apperian.eas.ApperianResourceID;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Signing+API
 */
public class Signing {
    public static Signing API = new Signing();

    Signing() {
    }

    public ListAllSigningCredentialsRequest listAllSigningCredentials() {
        return new ListAllSigningCredentialsRequest();
    }

    public SignApplicationRequest signApplication(ApperianResourceID credentialsId,
                                                  ApperianResourceID applicationId) {
        return new SignApplicationRequest(applicationId, credentialsId);
    }
}
