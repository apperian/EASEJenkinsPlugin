package com.apperian.eas.users;

import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.TestCredentials;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class UsersTest {
    static String sessionToken;

    public static String lazyAuth() throws IOException {
        if (sessionToken != null) {
            return sessionToken;
        }
        try {
            Users api = Users.API;
            String userId = TestCredentials.USER_ID;
            String password = TestCredentials.PASSWORD;
            AperianEndpoint endpoint = TestCredentials.APERIAN_ENDPOINT;

            AuthenticateUserResponse authResponse;

            authResponse = api.authenticateUser(userId, password)
                    .call(endpoint);

            sessionToken = authResponse.token;
            return sessionToken;
        } catch (IOException ex) {
            throw new RuntimeException("no network", ex);
        }
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        if (!TestCredentials.areSet()) {
            return;
        }

        assertNotNull("problems with authentication", lazyAuth());
    }
}