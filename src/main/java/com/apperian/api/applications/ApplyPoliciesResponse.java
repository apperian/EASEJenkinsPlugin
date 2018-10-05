package com.apperian.api.applications;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApplyPoliciesResponse {

    @JsonProperty("configurations")
    private List<PolicyConfiguration> configurations;

    public List<PolicyConfiguration> getConfigurations() {
        return configurations;
    }
}
