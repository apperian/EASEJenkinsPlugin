package com.apperian.eas.signing;

import com.apperian.eas.APIConstants;
import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.AperianRequest;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;

public class SignApplicationRequest extends AperianRequest {

    SignApplicationRequest(String sessionToken) {
        super(Type.PUT, "/credentials", sessionToken);
    }

    @Override
    public SignApplicationResponse call(AperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, SignApplicationResponse.class);
    }


    @Override
    protected Header[] takeHttpHeaders() {
        return new Header[] {
                APIConstants.CONTENT_TYPE_JSON_HEADER,
                new BasicHeader(APIConstants.X_TOKEN_HEADER, getSessionToken())
        };
    }

}
