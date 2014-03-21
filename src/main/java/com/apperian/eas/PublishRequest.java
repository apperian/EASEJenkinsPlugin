package com.apperian.eas;

import java.io.IOException;

public class PublishRequest extends PublishingRequest {
    public final Params params;

    public PublishRequest(String token, String transactionID, Metadata metadata, String applicationFileId) {
        super(APIConstants.PUBLISH_METHOD);
        this.params = new Params();
        this.params.token = token;
        this.params.transactionID = transactionID;
        this.params.EASEmetadata = metadata;
        this.params.files = new Files();
        this.params.files.application = applicationFileId;
    }

    @Override
    public PublishResponse call(PublishingEndpoint endpoint) throws IOException {
        return endpoint.doJsonRpc(this, PublishResponse.class);
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
                "token=" + params.token +
                ", transactionID=" + params.transactionID +
                ", applicationFileId=" + params.files.application +
                ", metadata=" + params.EASEmetadata +
                '}';
    }
}

