package com.apperian.api.application;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Applications+API
 */
public class Applications {
    public ApplicationListResponse list(ApperianEndpoint endpoint) throws ConnectionException {
        ApplicationListRequest request = new ApplicationListRequest();
        return request.call(endpoint);
    }

    public UpdateApplicationResponse updateApplication(ApperianEndpoint endpoint, ApperianResourceID applicationId, File appBinary, String author, String version, String versionNotes) throws ConnectionException {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);

        Map<String, Object> data = new HashMap<>();
        if (author != null) {
            data.put("author", author);
        }
        if (version != null) {
            data.put("version_num", version);
        }
        if (versionNotes != null) {
            data.put("version_note", versionNotes);
        }

        return request.call(endpoint, data, appBinary);
    }

    public UpdateApplicationResponse updateApplication(ApperianEndpoint endpoint, ApperianResourceID applicationId, boolean enabled) throws ConnectionException {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", enabled);
        return request.call(endpoint, data);
    }

    public GetApplicationInfoResponse getApplicationInfo(ApperianEndpoint endpoint, ApperianResourceID applicationId) throws ConnectionException {
        GetApplicationInfoRequest request =  new GetApplicationInfoRequest(applicationId);
        return request.call(endpoint);
    }

}
