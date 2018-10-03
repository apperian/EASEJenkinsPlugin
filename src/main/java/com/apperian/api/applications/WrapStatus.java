package com.apperian.api.applications;


public enum WrapStatus {
    ERROR(-1),
    NO_POLICIES(0),
    POLICIES_AND_SIGNED(1),
    APPLYING_POLICIES(2),
    POLICIES_NOT_SIGNED(3),
    POLICIES_PREVIOUSLY_APPLIED(4);

    private int statusCode;

    WrapStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
