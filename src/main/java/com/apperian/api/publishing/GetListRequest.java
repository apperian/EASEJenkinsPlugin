package com.apperian.api.publishing;

import com.apperian.api.APIConstants;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.EASERequest;

import java.io.IOException;

public class GetListRequest extends EASERequest {
    public final Params params;

    public GetListRequest() {
        super(APIConstants.GET_LIST_METHOD);
        this.params = new Params();
    }

    @Override
    public GetListResponse call(EASEEndpoint endpoint) throws IOException {
        this.params.token = endpoint.getSessionToken();
        return doJsonRpc(endpoint, this, GetListResponse.class);
    }

    public static class Params {
        public String token;
    }

    @Override
    public String toString() {
        return "GetListRequest{}";
    }
}

