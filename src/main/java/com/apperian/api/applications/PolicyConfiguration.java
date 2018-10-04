package com.apperian.api.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.sf.json.JSONObject;

public class PolicyConfiguration {
    @JsonProperty("policy_id")
    String policyId;

    @JsonProperty("configuration")
    JSONObject configuration;

    @JsonProperty("id")
    String id;

    @JsonProperty("auth_required")
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

