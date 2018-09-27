package com.apperian.api.signing;

import java.util.List;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;
import com.apperian.api.TestUtil;

import org.junit.Assert;
import org.junit.Test;

public class SigningTest {

    @Test
    public void testListCredentials() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApperianApi apperianApi = ApiTesting.getApperianApi();
        List<SigningCredential> credentials = apperianApi.listCredentials();

        Assert.assertNotNull(credentials);
    }

    @Test
    public void testSignApplication() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApperianApi apperianApi = ApiTesting.getApperianApi();
        SignApplicationResponse response = apperianApi.signApplication(ApiTesting.CREDENTIALS_PSK, ApiTesting.APP_ID);

        Assert.assertNotNull(response.getStatus());
    }

}
