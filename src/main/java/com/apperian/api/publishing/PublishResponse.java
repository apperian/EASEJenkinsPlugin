package com.apperian.api.publishing;

import com.apperian.api.EASEResponse;

public class PublishResponse extends EASEResponse {
    public Result result;

    public static class Result {
        public String appID;
        public String status;
    }

    @Override
    public String toString() {
        return "UpdateResponse{" +
                "appID=" + result.appID +
                "status=" + result.status +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}