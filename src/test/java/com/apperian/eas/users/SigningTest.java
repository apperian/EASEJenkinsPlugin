package com.apperian.eas.users;

import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.TestCredentials;
import com.apperian.eas.signing.ListAllSigningCredentialsResponse;
import com.apperian.eas.signing.SignApplicationResponse;
import com.apperian.eas.signing.Signing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SigningTest {
    Signing api;
    String sessionToken;
    AperianEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        api = Signing.API;
        sessionToken = UsersTest.lazyAuth();
        endpoint = TestCredentials.APERIAN_ENDPOINT;
    }

    @Test
    public void testListCredentials() throws Exception {
        ListAllSigningCredentialsResponse response;

        response = api.listAllSigningCredentials(sessionToken)
                .call(endpoint);

        Assert.assertNotNull(response.credentials);
    }

    public void testSignApplication() throws Exception {
        SignApplicationResponse response;

        response = api.signApplication(sessionToken)
                .call(endpoint);

    }
}
