package org.jenkinsci.plugins.api;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.EASEEndpoint;

public class ApperianEaseEndpoint {
    final EASEEndpoint easeEndpoint;
    final ApperianEndpoint apperianEndpoint;

    public ApperianEaseEndpoint(EASEEndpoint easeEndpoint,
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
        return "ApperianEaseEndpoint{" +
                "easeEndpoint=" + easeEndpoint +
                ", apperianEndpoint=" + apperianEndpoint +
                '}';
    }
}
