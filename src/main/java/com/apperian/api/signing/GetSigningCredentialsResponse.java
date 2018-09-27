package com.apperian.api.signing;

import java.util.List;

public class GetSigningCredentialsResponse {
    List<SigningCredential> credentials;

    public List<SigningCredential> getCredentials() {
        return credentials;
    }
}
