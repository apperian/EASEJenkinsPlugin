package com.apperian.api.signing;

import com.apperian.api.ApperianResourceID;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Signing+API
 */
public class Signing {
    public Signing() {
    }

    public ListAllSigningCredentialsRequest listAllSigningCredentials() {
        return new ListAllSigningCredentialsRequest();
    }

    public SignApplicationRequest signApplication(ApperianResourceID credentialsId,
                                                  ApperianResourceID applicationId) {
        return new SignApplicationRequest(applicationId, credentialsId);
    }
}
