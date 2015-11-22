package com.apperian.api.signing;

import com.apperian.api.ApperianEaseApi;
import org.junit.Assert;
import org.junit.Test;

import com.apperian.api.ApiTesting;
import com.apperian.api.TestUtil;

public class SigningTest {
    @Test
    public void testListCredentials() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ListAllSigningCredentialsResponse response;

        response = ApperianEaseApi.SIGNING.listCredentials()
                .call(ApiTesting.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);

        Assert.assertNotNull(response.getCredentials());
    }

    @Test
    public void testSignApplication() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        SignApplicationResponse response;

        response = ApperianEaseApi.SIGNING.signApplication(ApiTesting.CREDENTIALS_PSK, ApiTesting.APP_ID)
                .call(ApiTesting.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);
        Assert.assertNotNull(response.getStatus());
    }

}
