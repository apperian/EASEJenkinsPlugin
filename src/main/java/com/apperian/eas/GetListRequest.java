package com.apperian.eas;

import java.io.IOException;

public class GetListRequest extends PublishingRequest {
    public GetListRequest(String token) {
        super(APIConstants.GET_LIST_METHOD);
        this.params = new Params();
        this.params.token = token;
    }

    public final Params params;

        @Override
    public GetListResponse call(PublishingEndpoint endpoint) throws IOException {
        return endpoint.doJsonRpc(this, GetListResponse.class);
    }

    public static class Params {
        public String token;
    }

    @Override
    public String toString() {
        return "GetListRequest{" +
                "token=" + params.token +
                '}';
    }
}

