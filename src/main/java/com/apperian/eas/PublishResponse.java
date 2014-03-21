package com.apperian.eas;

public class PublishResponse extends PublishingResponse {
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