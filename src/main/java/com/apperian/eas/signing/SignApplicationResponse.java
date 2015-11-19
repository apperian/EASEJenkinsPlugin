package com.apperian.eas.signing;

import com.apperian.eas.AperianResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignApplicationResponse extends AperianResponse {
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
