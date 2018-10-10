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

        @JsonProperty("short_description")
        private String shortDescription;

        @JsonProperty("long_description")
        private String longDescription;

        @JsonProperty("version_num")
        private String versionNum;

        @JsonProperty("signing_status")
        private SigningStatus status;

        @JsonProperty("signing_status_details")
        private String statusDetails;

        @JsonProperty("wrap_status")
        private int wrapStatusCode;

        public String getAppName() {
            return appName;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getLongDescription() {
            return longDescription;
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

        public WrapStatus getWrapStatus() {
            return WrapStatus.fromValue(wrapStatusCode);
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

    // Check if the application can be wrapped base on it's type.
    public boolean canBeWrapped() {
        AppType type = this.getAppType();
        if (type == AppType.IOS || type == AppType.ANDROID) {
            return true;
        }
        return false;
    }

    // Check the wrap status to see if policies are applied to the application.
    public boolean hasPoliciesApplied() {
        WrapStatus status = this.getVersion().getWrapStatus();
        switch(status) {
            case APPLYING_POLICIES: return true;
            case POLICIES_NOT_SIGNED: return true;
            case POLICIES_AND_SIGNED: return true;
            case NO_POLICIES: return false;
            case POLICIES_PREVIOUSLY_APPLIED: return false;
            case ERROR: return false;
        }
        return false;
    }

    public Version getVersion() {
        return version;
    }

}