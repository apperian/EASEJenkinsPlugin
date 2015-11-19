package com.apperian.api.application;

import com.apperian.api.ApperianResponse;

import java.util.List;

public class ApplicationListResponse extends ApperianResponse {
    List<Application> applications;

    public List<Application> getApplications() {
        return applications;
    }
}
