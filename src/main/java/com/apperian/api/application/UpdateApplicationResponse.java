package com.apperian.api.application;

import com.apperian.api.ApperianResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateApplicationResponse extends ApperianResponse {
    private Application application;

    public Application getApplication() {
        return application;
    }
}
