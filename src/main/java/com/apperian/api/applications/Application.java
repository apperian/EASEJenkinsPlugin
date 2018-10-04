package com.apperian.api.applications;

import com.apperian.api.signing.SigningStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Application {
    private String id;

    private int type;

    private Version version;

    public static class Version {

        @JsonProperty("app_name")
        private String appName;

        @JsonProperty("version_num")
        private String versionNum;

        @JsonProperty("signing_status")
        private SigningStatus status;

        @JsonProperty("signing_status_details")
        private String statusDetails;

        public String getAppName() {
            return appName;
        }

        public String getVersionNum() {
            return versionNum;
        }

        public SigningStatus getStatus() {
            return status;
        }

        public String getStatusDetails() {
            return statusDetails;
        }
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        try {
            return AppType.fromValue(type).getDisplayName();
        } catch (IllegalArgumentException e) {
            return "Unknown";
        }
    }

    public AppType getAppType() {
        try {
            return AppType.fromValue(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isAppTypeSupportedByPlugin() {
        try {
            AppType.fromValue(type).getDisplayName();
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Version getVersion() {
        return version;
    }

}