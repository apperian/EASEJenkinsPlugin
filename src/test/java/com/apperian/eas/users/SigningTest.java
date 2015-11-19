package com.apperian.eas.users;

import com.apperian.eas.TestCredentials;
import com.apperian.eas.TestUtil;
import com.apperian.eas.signing.ListAllSigningCredentialsResponse;
import com.apperian.eas.signing.SignApplicationResponse;
import com.apperian.eas.signing.Signing;
import org.junit.Assert;
import org.junit.Test;

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
