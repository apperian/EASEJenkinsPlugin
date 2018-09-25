package com.apperian.api.publishing;

import com.apperian.api.APIConstants;
import com.apperian.api.ConnectionException;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.EASERequest;

import java.io.IOException;

public class ApplicationListRequest extends EASERequest {
    public final Params params;

    public ApplicationListRequest() {
        super(APIConstants.GET_LIST_METHOD);
        this.params = new Params();
    }

    @Override
    public ApplicationListResponse call(EASEEndpoint endpoint) throws ConnectionException {
        this.params.token = endpoint.getSessionToken();
        return doJsonRpc(endpoint, this, ApplicationListResponse.class);
    }

    public static class Params {
        public String token;
    }

    @Override
    public String toString() {
        return "ApplicationListRequest{}";
    }
}

