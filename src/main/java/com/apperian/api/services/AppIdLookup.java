package com.apperian.api.services;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.application.Application;
import com.apperian.api.application.ApplicationListResponse;
import com.apperian.api.application.Applications;

import java.io.IOException;
import java.util.List;

public class AppIdLookup {
    ApperianEndpoint endpoint;

    public AppIdLookup(ApperianEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public ApperianResourceID lookupAppId(String easeId) {
        try {
            ApplicationListResponse response = Applications.API.list()
                    .call(endpoint);

            List<Application> apps = response.getApplications();
            if (apps == null) {
                return null;
            }

            for (Application app : apps) {
                // here is supposed to search applications
                // but seems no easeId could be found
            }
            return null; // TODO
        } catch(IOException ex) {
            throw new RuntimeException("no network", ex);
        }
    }
}
