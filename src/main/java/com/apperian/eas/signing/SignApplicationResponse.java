package com.apperian.eas.signing;

import com.apperian.eas.ApperianResponse;
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
