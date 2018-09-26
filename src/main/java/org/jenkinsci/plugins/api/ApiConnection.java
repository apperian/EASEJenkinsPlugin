package org.jenkinsci.plugins.api;

import com.apperian.api.ApperianEndpoint;

public class ApiConnection {
    final ApperianEndpoint apperianEndpoint;

    public ApiConnection(ApperianEndpoint apperianEndpoint) {
        this.apperianEndpoint = apperianEndpoint;
    }

    public ApperianEndpoint getApperianEndpoint() {
        return apperianEndpoint;
    }

    @Override
    public String toString() {
        return "ApiConnection{" +
                "apperianEndpoint=" + apperianEndpoint +
                '}';
    }
}
