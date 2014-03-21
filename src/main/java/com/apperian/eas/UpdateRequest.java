package com.apperian.eas;

import java.io.IOException;

public class UpdateRequest extends PublishingRequest {
    public final Params params;

    public UpdateRequest(String token, String appID) {
        super(APIConstants.UPDATE_METHOD);
        this.params = new Params();
        this.params.token = token;
        this.params.appID = appID;
    }

    @Override
    public UpdateResponse call(PublishingEndpoint endpoint) throws IOException {
        return endpoint.doJsonRpc(this, UpdateResponse.class);
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

