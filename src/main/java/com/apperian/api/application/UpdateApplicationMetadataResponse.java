package com.apperian.api.application;

import com.apperian.api.ApperianResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateApplicationMetadataResponse extends ApperianResponse {
    @JsonProperty("update_application_result")
    public boolean updateResult;
}
