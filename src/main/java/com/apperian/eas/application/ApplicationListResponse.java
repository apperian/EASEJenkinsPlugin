package com.apperian.eas.application;

import com.apperian.eas.ApperianResponse;

import java.util.List;

public class ApplicationListResponse extends ApperianResponse {
    List<Application> applications;

    public List<Application> getApplications() {
        return applications;
    }
}
