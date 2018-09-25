package com.apperian.api;

public class ApiTesting {
    // TODO JJJ check what value we should use here
    public static String API_TOKEN = null;

    public static ApperianResourceID ORGANIZATION_ID = new ApperianResourceID("5763"); // Rails Reactor
    public static ApperianResourceID USER_PSK = new ApperianResourceID("392157");
    public static ApperianResourceID CREDENTIALS_PSK = new ApperianResourceID("gFuZNZYDdcbOhOu_TfzisQ"); // who knows
    public static ApperianResourceID APP_PSK = new ApperianResourceID("48489");
    public static ApperianResourceID APP_ID = new ApperianResourceID("XDnZ9-NxYIxpasODR9M6Yw");

    private static String EASE_ENDPOINT_URL = "https://easesvc.apperian.com/ease.interface.php";
    private static String APPERIAN_ENDPOINT_URL = "https://na01ws.apperian.com";

    public static boolean areCredentialsSet() {
        return ApiTesting.getApperianEndpoint().isLoggedIn() && ApiTesting.getEASEEndpoint().isLoggedIn();
    }

    // TODO:  Fix this name, makes no sense
    public static ApperianEndpoint getApperianEndpoint() {
        return new ApperianEndpoint(APPERIAN_ENDPOINT_URL, API_TOKEN);
    }

    // TODO:  Fix this name, makes no sense
    public static EASEEndpoint getEASEEndpoint() {
        return new EASEEndpoint(EASE_ENDPOINT_URL, API_TOKEN);
    }
}
