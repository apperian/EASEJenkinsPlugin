package com.apperian.eas;

public class AuthenticateUserResponse extends PublishingResponse {
    public Result result;

    public static class Result {
        public String token;
    }

    @Override
    public String toString() {
        return "AuthenticateUserResponse{" +
                "token='" + result.token + '\'' +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}
