package com.apperian.api.signing;

import com.apperian.api.ApperianResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignApplicationResponse extends ApperianResponse {
    @JsonProperty("signing_status")
    SigningStatus status;

    @JsonProperty("signing_status_details")
    String statusDetails;

    public SigningStatus getStatus() {
        return status;
    }

    public String getStatusDetails() {
        return statusDetails;
    }
}
