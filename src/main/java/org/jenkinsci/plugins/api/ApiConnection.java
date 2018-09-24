package org.jenkinsci.plugins.api;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.EASEEndpoint;

public class ApiConnection {
    final EASEEndpoint easeEndpoint;
    final ApperianEndpoint apperianEndpoint;

    public ApiConnection(EASEEndpoint easeEndpoint,
                                ApperianEndpoint apperianEndpoint) {
        this.easeEndpoint = easeEndpoint;
        this.apperianEndpoint = apperianEndpoint;
    }

    public EASEEndpoint getEaseEndpoint() {
        return easeEndpoint;
    }

    public ApperianEndpoint getApperianEndpoint() {
        return apperianEndpoint;
    }

    @Override
    public String toString() {
        return "ApiConnection{" +
                "easeEndpoint=" + easeEndpoint +
                ", apperianEndpoint=" + apperianEndpoint +
                '}';
    }
}
