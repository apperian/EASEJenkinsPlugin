package com.apperian.eas;

import org.junit.Assert;

public class TestUtil {
    public static void assertNoError(ResponseWithError response) {
        if (response.hasError()) {
            Assert.fail(response.getErrorMessage());
        }
    }

    public static boolean shouldSkipIntegrationTests() {
        if (!TestCredentials.CREDENTIALS_SET) {
            System.out.println("Credentials not set: " + TestCredentials.EASE_ENDPOINT.getLastLoginError());
            return true;
        }
        return false;
    }
}
