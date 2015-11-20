package com.apperian.api.publishing;

import com.apperian.api.EASEResponse;
import com.apperian.api.metadata.Metadata;

public class UpdateApplicationResponse extends EASEResponse {
    public Result result;

    public static class Result {
        public String transactionID;
        public String fileUploadURL;
        public Metadata EASEmetadata;
    }

    @Override
    public String toString() {
        return "UpdateApplicationResponse{" +
                "transactionID=" + result.transactionID +
                ", fileUploadURL=" + result.fileUploadURL +
                ", EASEmetadata=" + result.EASEmetadata +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}