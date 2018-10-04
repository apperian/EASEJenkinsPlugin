package com.apperian.api.applications;

public enum AppType {
    IOS(0, "iOS"),
    ANDROID(3, "Android"),
    WIN_PHONE(10, "Windows Phone"),
    WIN_EXE(12, "Windows EXE"),
    WIN_MSI(13, "Windows MSI"),
    WIN_ZIP(14, "Windows ZIP");

    final int ordValue;
    final String displayName;

    AppType(int ordValue, String displayName) {
        this.ordValue = ordValue;
        this.displayName = displayName;
    }

    public int getOrdinalValue() {
        return ordValue;
    }

    public static AppType fromValue(int appTypeAsOrd) {
        for (AppType appType : AppType.values()) {
            if (appType.ordValue == appTypeAsOrd) {
                return appType;
            }
        }
        throw new IllegalArgumentException("bad app type: " + appTypeAsOrd);
    }

    public String getDisplayName() {
        return displayName;
    }
}