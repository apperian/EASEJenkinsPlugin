package com.apperian.api.signing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlatformType {
    IOS(1),
    ANDROID(2);

    int ordValue;

    PlatformType(int ordValue) {
        this.ordValue = ordValue;
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
}