package com.apperian.api.signing;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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

        List<SigningCredential> credentials = apperianApi.listCredentials(ApiTesting.getApperianEndpoint());

        Assert.assertNotNull(credentials);
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
