package com.apperian.api.application;

public enum AppType {
    IOS(0, "iOS"),
    WEB_APP(1, "Web App"),
    iTUNES(2, "iTunes"),
    ANDROID(3, "Android"),
    IOS_CONFIG(4, "iOS Config"),
    ANDROID_MARKET(5, "Android Market"),
    WIN_PHONE_STORE(9, "Windows Phone Store"),
    WIN_PHONE(10, "Windows Phone"),
    WIN_STORE(11, "Windows Store"),
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