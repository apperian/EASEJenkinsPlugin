package com.apperian.eas.publishing;

import com.apperian.eas.EASEResponse;

public class UpdateResponse extends EASEResponse {
    public Result result;

    public static class Result {
        public String transactionID;
        public String fileUploadURL;
        public Metadata EASEmetadata;
    }

    @Override
    public String toString() {
        return "UpdateResponse{" +
                "transactionID=" + result.transactionID +
                ", fileUploadURL=" + result.fileUploadURL +
                ", EASEmetadata=" + result.EASEmetadata +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}