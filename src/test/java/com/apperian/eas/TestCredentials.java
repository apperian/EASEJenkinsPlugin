package com.apperian.eas;

public class TestCredentials {
    public static final String USER_ID = "testuser";
    public static final String PASSWORD = "testpassword";
    public static final String EASE_ENDPOINT = "https://easesvc.apperian.com/ease.interface.php";
    public static final String APERIAN_ENDPOINT = "https://na01ws.apperian.com/v1";

    public static boolean areSet() {
        return !USER_ID.equals("testuser");
    }
}
