package com.apperian.eas.publishing;

import com.apperian.eas.EASEEndpoint;
import com.apperian.eas.TestCredentials;
import org.junit.Before;
import org.junit.Test;

public class PublishingAPITest {

    EASEEndpoint endpoint;

    @Before
    public void setUp() {
        endpoint = new EASEEndpoint(TestCredentials.EASE_ENDPOINT);
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        if (!TestCredentials.areSet()) {
            return;
        }
        AuthenticateUserResponse response = PublishingAPI.authenticateUser(TestCredentials.USER_ID, TestCredentials.PASSWORD).call(endpoint);
        System.out.println(response);
    }
}
