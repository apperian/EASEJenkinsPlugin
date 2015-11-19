package com.apperian.api.users;

import org.junit.Assert;
import org.junit.Test;

import com.apperian.api.TestCredentials;
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
                .call(TestCredentials.APERIAN_ENDPOINT);

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

        response = Signing.API.signApplication(TestCredentials.CREDENTIALS_PSK, TestCredentials.APP_PSK)
                .call(TestCredentials.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);

        Assert.assertNotNull(response.getStatus());
    }

}
