package com.apperian.api.applications;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetPoliciesResponse {
    @JsonProperty("status")
    private PolicyStatus policyStatus;

    @JsonProperty("configurations")
    private List<PolicyConfiguration> policyConfigurations;

    @JsonProperty("previous_version_configurations")
    private List<PolicyConfiguration> previousVersionConfigurations;

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public List<PolicyConfiguration> getPolicyConfigurations() {
        return policyConfigurations;
    }

    public List<PolicyConfiguration> getPreviousVersionConfigurations() {
        return previousVersionConfigurations;
    }

    // NOTE:  Even though the 'PolicyStatus' class is only used internally here, we need it to be public so that the
    //        mapper can map the JSON to it.
    public class PolicyStatus {
        @JsonProperty("code")
        int code;

        @JsonProperty("description")
        String description;

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
