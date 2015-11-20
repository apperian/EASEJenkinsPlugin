package com.apperian.api;

public interface ApiTesting {
    String USER_ID = "oleksiy";
    String PASSWORD = "testpassword";


    ApperianResourceID ORGANIZATION_ID = new ApperianResourceID("5763"); // Rails Reactor
    ApperianResourceID USER_PSK = new ApperianResourceID("392157");
    ApperianResourceID CREDENTIALS_PSK = new ApperianResourceID("gFuZNZYDdcbOhOu_TfzisQ"); // who knows
    ApperianResourceID APP_PSK = new ApperianResourceID("48489");
    ApperianResourceID APP_ID = new ApperianResourceID("XDnZ9-NxYIxpasODR9M6Yw");

    String EASE_ENDPOINT_URL = "https://easesvc.apperian.com/ease.interface.php";
    String APERIAN_ENDPOINT_URL = "https://na01ws.apperian.com/v1";

    ApperianEndpoint APERIAN_ENDPOINT = TestEndpointFactory.apperian();
    EASEEndpoint EASE_ENDPOINT = TestEndpointFactory.ease();

    boolean CREDENTIALS_SET = APERIAN_ENDPOINT.isLoggedIn() && EASE_ENDPOINT.isLoggedIn();

    class TestEndpointFactory {
        public static ApperianEndpoint apperian() {
            ApperianEndpoint result = new ApperianEndpoint(APERIAN_ENDPOINT_URL);
            result.tryLogin(USER_ID, PASSWORD);
            return result;
        }
        public static EASEEndpoint ease() {
            EASEEndpoint result = new EASEEndpoint(EASE_ENDPOINT_URL);
            result.tryLogin(USER_ID, PASSWORD);
            return result;
        }
    }
}
