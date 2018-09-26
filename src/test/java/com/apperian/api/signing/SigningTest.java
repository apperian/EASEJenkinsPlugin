package com.apperian.api.signing;

import org.junit.Assert;
import org.junit.Test;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;
import com.apperian.api.TestUtil;

public class SigningTest {
    private ApperianApi apperianApi = new ApperianApi();

    @Test
    public void testListCredentials() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ListAllSigningCredentialsResponse response;

        response = apperianApi.listCredentials(ApiTesting.getApperianEndpoint());

        Assert.assertNotNull(response.getCredentials());
    }

    @Test
    public void testSignApplication() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        SignApplicationResponse response;

        response = apperianApi.signApplication(ApiTesting.getApperianEndpoint(), ApiTesting.CREDENTIALS_PSK, ApiTesting.APP_ID);

        Assert.assertNotNull(response.getStatus());
    }

}
