package com.apperian.eas.signing;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SigningStatus {
    @JsonProperty("in_progress")
    IN_PROGRESS,

    @JsonProperty("signed")
    SIGNED,

    @JsonProperty("cancelled")
    CANCELLED,

    @JsonProperty("error")
    ERROR
}
