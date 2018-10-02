package com.apperian.api.applications;

import net.sf.json.JSONObject;

import java.util.List;

public class GetPoliciesResponse {
    int statusCode;
    String description;
    List<PolicyConfiguration> policyConfigurations;

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public List<PolicyConfiguration> getPolicyConfigurations() {
        return policyConfigurations;
    }


    private class PolicyConfiguration {
        String policyId;
        JSONObject configuration;
        String id;
        String authRequired;

        public String getPolicyId() {
            return policyId;
        }

        public JSONObject getConfiguration() {
            return configuration;
        }

        public String getId() {
            return id;
        }

        public String getAuthRequired() {
            return authRequired;
        }
    }
}
