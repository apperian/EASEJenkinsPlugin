package com.apperian.api.signing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlatformType {
    IOS(1, "iOS"),
    ANDROID(2, "Android"),
    WINDOWS_PHONE(4, "Windows Phone"),
    WINDOWS(5, "Windows");

    final int ordValue;
    final String displayName;

    PlatformType(int ordValue, String displayName) {
        this.ordValue = ordValue;
        this.displayName = displayName;
    }

    @JsonValue
    public int getOrdinalValue() {
        return ordValue;
    }

    @JsonCreator
    public static PlatformType fromValue(int platformAsOrd) {
        for (PlatformType platform : PlatformType.values()) {
            if (platform.ordValue == platformAsOrd) {
                return platform;
            }
        }
        throw new IllegalArgumentException("bad platform: " + platformAsOrd);
    }

    public String getDisplayName() {
        return displayName;
    }
}