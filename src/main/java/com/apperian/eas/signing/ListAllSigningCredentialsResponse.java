package com.apperian.eas.signing;

import com.apperian.eas.AperianResponse;

import java.util.List;

public class ListAllSigningCredentialsResponse extends AperianResponse {
    List<SigningCredential> credentials;

    public List<SigningCredential> getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return "ListAllSigningCredentialsResponse{" +
                "credentials=" + credentials +
                '}';
    }
}
