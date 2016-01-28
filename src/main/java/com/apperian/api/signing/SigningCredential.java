package com.apperian.api.signing;

import com.apperian.api.ApperianResourceID;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SigningCredential {
    @JsonProperty("psk")
    ApperianResourceID credentialId;

    @JsonProperty("description")
    String description;

    @JsonProperty("expiration_date")
    String expirationDate;

    @JsonProperty("platform")
    PlatformType platform;

    public ApperianResourceID getCredentialId() {
        return credentialId;
    }

    public String getDescription() {
        return description;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public PlatformType getPlatform() {
        return platform;
    }

    @Override public String toString() {
        return "SigningCredential{" +
               "credentialId=" + credentialId +
               ", description='" + description + '\'' +
               ", expirationDate=" + expirationDate +
               ", platform=" + platform +
               '}';
    }
}
