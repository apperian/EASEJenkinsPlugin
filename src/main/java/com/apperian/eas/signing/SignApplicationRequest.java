package com.apperian.eas.signing;

import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.AperianRequest;

import java.io.IOException;

public class SignApplicationRequest extends AperianRequest {
    SignApplicationRequest(String sessionToken) {
        super(Type.PUT, "/credentials", sessionToken);
    }

    @Override
    public SignApplicationResponse call(AperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, SignApplicationResponse.class);
    }
}
