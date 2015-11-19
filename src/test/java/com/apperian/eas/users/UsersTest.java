package com.apperian.eas.users;

import com.apperian.eas.TestCredentials;
import com.apperian.eas.TestUtil;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class UsersTest {
    @Test
    public void testAuthenticateUser() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        assertNotNull(TestCredentials.APERIAN_ENDPOINT.getSessionToken());
    }
}