package com.apperian.api.application;

import com.apperian.api.ApperianResourceID;
import com.apperian.api.signing.SigningStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Application {
    @JsonProperty("id")
    ApperianResourceID id;

    Version version;

    public static class Version {

        @JsonProperty("signing_status")
        SigningStatus status;

        @JsonProperty("signing_status_details")
        String statusDetails;

        public SigningStatus getStatus() {
            return status;
        }

        public String getStatusDetails() {
            return statusDetails;
        }
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                '}';
    }
}
// TODO add other attributes from here https://help.apperian.com/pages/viewpage.action?pageId=3441084