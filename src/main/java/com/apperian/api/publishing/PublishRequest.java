package com.apperian.api.publishing;

import com.apperian.api.APIConstants;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.EASERequest;

import java.io.IOException;

public class PublishRequest extends EASERequest {
    public final Params params;

    public PublishRequest(String transactionID, Metadata metadata, String applicationFileId) {
        super(APIConstants.PUBLISH_METHOD);
        this.params = new Params();
        this.params.transactionID = transactionID;
        this.params.EASEmetadata = metadata;
        this.params.files = new Files();
        this.params.files.application = applicationFileId;
    }

    @Override
    public PublishResponse call(EASEEndpoint endpoint) throws IOException {
        this.params.token = endpoint.getSessionToken();
        return doJsonRpc(endpoint, this, PublishResponse.class);
    }

    public static class Params {
        public String token;
        public String transactionID;
        public Metadata EASEmetadata;
        public Files files;
    }

    public static class Files {
        public String application;
    }

    @Override
    public String toString() {
        return "PublishRequest{" +
                ", transactionID=" + params.transactionID +
                ", applicationFileId=" + params.files.application +
                ", metadata=" + params.EASEmetadata +
                '}';
    }
}

