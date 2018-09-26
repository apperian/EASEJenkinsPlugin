package com.apperian.api;

public class TestUtil {

    public static boolean shouldSkipIntegrationTests() {
        if (!ApiTesting.areCredentialsSet()) {
            System.out.println("Credentials not set");
            return true;
        }
        return false;
    }
}
