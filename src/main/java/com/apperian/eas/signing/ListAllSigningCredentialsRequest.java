package com.apperian.eas.signing;


import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.AperianRequest;

import java.io.IOException;

public class ListAllSigningCredentialsRequest extends AperianRequest {
    public ListAllSigningCredentialsRequest(String sessionToken) {
        super(Type.GET, "/credentials", sessionToken);
    }

    @Override
    public ListAllSigningCredentialsResponse call(AperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, ListAllSigningCredentialsResponse.class);
    }

    @Override
    public String toString() {
        return "ListAllSigningCredentialsRequest{" +
                "type=" + getType() +
                ", apiPath='" + getApiPath() + '\'' +
                ", sessionToken='" + getSessionToken() + '\'' +
                '}';
    }
}
