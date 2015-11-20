package com.apperian.api.publishing;

import com.apperian.api.APIConstants;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.EASERequest;

import java.io.IOException;

public class UpdateApplicationRequest extends EASERequest {
    public final Params params;

    public UpdateApplicationRequest(String appID) {
        super(APIConstants.UPDATE_METHOD);
        this.params = new Params();
        this.params.appID = appID;
    }

    @Override
    public UpdateApplicationResponse call(EASEEndpoint endpoint) throws IOException {
        this.params.token = endpoint.getSessionToken();
        return doJsonRpc(endpoint, this, UpdateApplicationResponse.class);
    }

    public static class Params {
        public String token;
        public String appID;
    }

    @Override
    public String toString() {
        return "UpdateApplicationRequest{" +
                "appID=" + params.appID +
                "}";
    }
}

