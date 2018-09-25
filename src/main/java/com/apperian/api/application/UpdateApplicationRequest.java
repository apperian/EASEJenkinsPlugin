package com.apperian.api.application;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;
import com.apperian.api.metadata.Metadata;

import java.io.File;
import java.io.IOException;

public class UpdateApplicationRequest extends ApperianRequest {
    private transient File appBinary;
    private String author;
    private String version;
    private String versionNotes;
    private boolean enabled;

    public UpdateApplicationRequest(ApperianResourceID applicationId) {
        super(Type.POST, "/v1/applications/" + applicationId);
    }

    public void setAppBinary(File appBinary) {
        this.appBinary = appBinary;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVersionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public UpdateApplicationResponse call(ApperianEndpoint endpoint) throws ConnectionException {
        return endpoint.uploadFile(this,  "app_file", appBinary, UpdateApplicationResponse.class);
    }
}
