package com.apperian.eas;

import com.apperian.eas.PublishingResponse;

public class AuthenticateUserResponse extends PublishingResponse {
    String token;

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "AuthenticateUserResponse{" +
                "token='" + token + '\'' +
                ", error='" + getError() + '\'' +
                '}';
    }
}
