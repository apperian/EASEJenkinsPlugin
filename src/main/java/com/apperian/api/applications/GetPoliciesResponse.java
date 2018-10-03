package com.apperian.api.applications;

import java.util.List;

public class GetPoliciesResponse {
    private int statusCode;
    private String description;
    private List<PolicyConfiguration> policyConfigurations;

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public List<PolicyConfiguration> getPolicyConfigurations() {
        return policyConfigurations;
    }
}
