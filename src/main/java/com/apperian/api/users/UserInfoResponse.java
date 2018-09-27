package com.apperian.api.users;

import com.apperian.api.ApperianResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfoResponse extends ApperianResponse {
    @JsonProperty("id")
    private String id;
}
