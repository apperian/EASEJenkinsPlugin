package com.apperian.api.signing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignApplicationResponse {
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
