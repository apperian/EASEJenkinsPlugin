package com.apperian.eas.publishing;

import com.apperian.eas.APIConstants;
import com.apperian.eas.EASEEndpoint;
import com.apperian.eas.EASERequest;

import java.io.IOException;

public class UpdateRequest extends EASERequest {
    public final Params params;

    public UpdateRequest(String token, String appID) {
        super(APIConstants.UPDATE_METHOD);
        this.params = new Params();
        this.params.token = token;
        this.params.appID = appID;
    }

    @Override
    public UpdateResponse call(EASEEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, UpdateResponse.class);
    }

    public static class Params {
        public String token;
        public String appID;
    }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "token=" + params.token +
                "appID=" + params.appID +
                '}';
    }
}

