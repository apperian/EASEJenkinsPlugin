package com.apperian.api;

import org.junit.Assert;

public class TestUtil {
    public static void assertNoError(ResponseWithError response) {
        Assert.assertNotNull("response is null", response);

        if (response.hasError()) {
            Assert.fail(response.getErrorMessage());
        }
    }

    public static boolean shouldSkipIntegrationTests() {
        if (!ApiTesting.areCredentialsSet()) {
            System.out.println("Credentials not set: " + ApiTesting.getEASEEndpoint().getLastLoginError());
            return true;
        }
        return false;
    }
}
