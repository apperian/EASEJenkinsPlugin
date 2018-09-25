package com.apperian.api.publishing;

import com.apperian.api.metadata.Metadata;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Publishing+API+Guide
 */
public class Publishing {

    public ApplicationListRequest list() {
        return new ApplicationListRequest();
    }
}
