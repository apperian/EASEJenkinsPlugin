package com.apperian.api;

public class ApiTesting {
    public static String API_TOKEN = null;

    public static ApperianResourceID ORGANIZATION_ID = new ApperianResourceID("5763"); // Rails Reactor
    public static ApperianResourceID USER_PSK = new ApperianResourceID("392157");
    public static ApperianResourceID CREDENTIALS_PSK = new ApperianResourceID("gFuZNZYDdcbOhOu_TfzisQ"); // who knows
    public static ApperianResourceID APP_PSK = new ApperianResourceID("48489");
    public static ApperianResourceID APP_ID = new ApperianResourceID("XDnZ9-NxYIxpasODR9M6Yw");

    private static String EASE_ENDPOINT_URL = "https://easesvc.apperian.com/ease.interface.php";
    private static String APPERIAN_ENDPOINT_URL = "https://na01ws.apperian.com";

    public static boolean areCredentialsSet() {
        return API_TOKEN != null;
    }

    public static ApperianApi getApperianApi() {
        return new ApperianApi(APPERIAN_ENDPOINT_URL, API_TOKEN);
    }
}
