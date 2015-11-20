package com.apperian.api.application;

import com.apperian.api.ApperianResourceID;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Applications+API
 */
public class Applications {
    public ApplicationListRequest list() {
        return new ApplicationListRequest();
    }

    public UpdateApplicationMetadataRequest updateApplicationMetadata(ApperianResourceID applicationId) {
        return new UpdateApplicationMetadataRequest(applicationId);
    }

}
