package com.apperian.eas.signing;

import com.apperian.eas.AperianResponse;

import java.util.List;

public class ListAllSigningCredentialsResponse extends AperianResponse {
    public List<SigningCredential> credentials;

    @Override
    public String toString() {
        return "ListAllSigningCredentialsResponse{" +
                "credentials=" + credentials +
                '}';
    }
}
