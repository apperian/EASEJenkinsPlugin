package com.apperian.eas.signing;

import com.apperian.eas.ApperianResourceID;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SigningCredential {
    @JsonProperty("psk")
    ApperianResourceID credentialId;

    @JsonProperty("description")
    String description;

    @JsonProperty("expiration_date")
    Date expirationDate;

    PlatformType platform;

    public ApperianResourceID getCredentialId() {
        return credentialId;
    }
}
