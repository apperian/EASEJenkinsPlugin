package com.apperian.api.signing;

import java.util.Date;

import com.apperian.api.ApperianResourceID;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SigningCredential {
    @JsonProperty("psk")
    ApperianResourceID credentialId;

    @JsonProperty("description")
    String description;

    @JsonProperty("expiration_date")
    Date expirationDate;

    @JsonProperty("platform")
    PlatformType platform;

    public ApperianResourceID getCredentialId() {
        return credentialId;
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
