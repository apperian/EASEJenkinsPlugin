package com.apperian.api;

public class ApiTesting {
    public static String API_TOKEN = null;

    public static String ORGANIZATION_ID = new String("5763"); // Rails Reactor
    public static String USER_PSK = new String("392157");
    public static String CREDENTIALS_PSK = new String("gFuZNZYDdcbOhOu_TfzisQ"); // who knows
    public static String APP_PSK = new String("48489");
    public static String APP_ID = new String("XDnZ9-NxYIxpasODR9M6Yw");

    private static String EASE_ENDPOINT_URL = "https://easesvc.apperian.com/ease.interface.php";
    private static String APPERIAN_ENDPOINT_URL = "https://na01ws.apperian.com";

    public static boolean areCredentialsSet() {
        return API_TOKEN != null;
    }

    public static ApperianApi getApperianApi() {
        return new ApperianApi(APPERIAN_ENDPOINT_URL, API_TOKEN);
    }
}
