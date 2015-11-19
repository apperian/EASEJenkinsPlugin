package com.apperian.eas;

public class TestCredentials {
    // TODO FIXME do not commit!
    public static final String USER_ID = "dmitriy.scherbina@railsreactor.com";
    public static final String PASSWORD = "dmitriy.scherbina1";
    public static final int ORGANIZATION_PSK = 5763; // Rails Reactor
    public static final int USER_PSK = 392157;

    public static final String EASE_ENDPOINT_URL = "https://easesvc.apperian.com/ease.interface.php";
    public static final String APERIAN_ENDPOINT_URL = "https://na01ws.apperian.com/v1";

    public static final AperianEndpoint APERIAN_ENDPOINT = new AperianEndpoint(APERIAN_ENDPOINT_URL);


    public static boolean areSet() {
        return !USER_ID.equals("testuser");
    }
}
