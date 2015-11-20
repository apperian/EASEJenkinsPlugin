package com.apperian.api.publishing;

import com.apperian.api.EASEResponse;

public class PublishApplicationResponse extends EASEResponse {
    public Result result;

    public static class Result {
        public String appID;
        public String status;
    }

    @Override
    public String toString() {
        return "PublishApplicationResponse{" +
                "appID=" + result.appID +
                "status=" + result.status +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}