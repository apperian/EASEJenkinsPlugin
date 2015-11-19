package com.apperian.api.publishing;

import com.apperian.api.APIConstants;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.EASERequest;

import java.io.IOException;

public class UpdateRequest extends EASERequest {
    public final Params params;

    public UpdateRequest(String appID) {
        super(APIConstants.UPDATE_METHOD);
        this.params = new Params();
        this.params.appID = appID;
    }

    @Override
    public UpdateResponse call(EASEEndpoint endpoint) throws IOException {
        this.params.token = endpoint.getSessionToken();
        return doJsonRpc(endpoint, this, UpdateResponse.class);
    }

    public static class Params {
        public String token;
        public String appID;
    }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "appID=" + params.appID +
                "}";
    }
}

