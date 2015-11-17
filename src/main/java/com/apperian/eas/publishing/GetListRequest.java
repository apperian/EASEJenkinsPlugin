package com.apperian.eas.publishing;

import com.apperian.eas.APIConstants;
import com.apperian.eas.PublishingEndpoint;
import com.apperian.eas.PublishingRequest;

import java.io.IOException;

public class GetListRequest extends PublishingRequest {
    public final Params params;

    public GetListRequest(String token) {
        super(APIConstants.GET_LIST_METHOD);
        this.params = new Params();
        this.params.token = token;
    }

        @Override
    public GetListResponse call(PublishingEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, GetListResponse.class);
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

