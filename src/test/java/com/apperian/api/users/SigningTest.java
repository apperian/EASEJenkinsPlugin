package com.apperian.api.users;

import org.junit.Assert;
import org.junit.Test;

import com.apperian.api.ApiTesting;
import com.apperian.api.TestUtil;
import com.apperian.api.signing.ListAllSigningCredentialsResponse;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.Signing;

public class SigningTest {
    @Test
    public void testListCredentials() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ListAllSigningCredentialsResponse response;

        response = Signing.API.listAllSigningCredentials()
                .call(ApiTesting.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);

        Assert.assertNotNull(response.getCredentials());

        System.out.println(response.getCredentials());
    }

    @Test
    public void testSignApplication() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        SignApplicationResponse response;

        response = Signing.API.signApplication(ApiTesting.CREDENTIALS_PSK, ApiTesting.APP_ID)
                .call(ApiTesting.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);

        Assert.assertNotNull(response.getStatus());
    }

}
