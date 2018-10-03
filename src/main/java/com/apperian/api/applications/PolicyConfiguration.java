package com.apperian.api.applications;

import net.sf.json.JSONObject;

public class PolicyConfiguration {
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

