package com.apperian.api.signing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SigningStatus {
    IN_PROGRESS("in_progress"),
    SIGNED("signed"),
    CANCELLED("cancelled"),
    ERROR("error");

    private String value;

    SigningStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static final SigningStatus fromString(String value) {
        for (SigningStatus status : SigningStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new RuntimeException("bad value: " + value);
    }
}
