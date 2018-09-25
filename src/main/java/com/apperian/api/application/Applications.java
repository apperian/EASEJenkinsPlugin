package com.apperian.api.application;

import java.io.File;

import com.apperian.api.ApperianResourceID;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Applications+API
 */
public class Applications {
    public ApplicationListRequest list() {
        return new ApplicationListRequest();
    }

    public UpdateApplicationRequest updateApplication(ApperianResourceID applicationId, File appBinary, String author, String version, String versionNotes) {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);
        request.setAppBinary(appBinary);
        request.setAuthor(author);
        request.setVersion(version);
        request.setVersionNotes(versionNotes);
        return request;
    }

    public UpdateApplicationRequest updateApplication(ApperianResourceID applicationId, boolean enabled) {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);
        request.setEnabled(enabled);
        return request;
    }

    public GetApplicationInfoRequest getApplicationInfo(ApperianResourceID applicationId) {
        return new GetApplicationInfoRequest(applicationId);
    }

}
