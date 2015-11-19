package com.apperian.eas.users;

import com.apperian.eas.TestCredentials;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class UsersAPITest {
    static String token;

    public static String lazyAuth() throws IOException {
        if (token != null) {
            return token;
        }
        try {
            AuthenticateUserResponse authResponse =
                    UsersAPI.authenticateUser(TestCredentials.USER_ID, TestCredentials.PASSWORD)
                            .call(TestCredentials.APERIAN_ENDPOINT);
            token = authResponse.token;
            return token;
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