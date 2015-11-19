package com.apperian.eas.signing;


import com.apperian.eas.APIConstants;
import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.AperianRequest;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

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
    protected Header[] takeHttpHeaders() {
        return new Header[] {
                new BasicHeader(APIConstants.X_TOKEN_HEADER, getSessionToken())
        };
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
