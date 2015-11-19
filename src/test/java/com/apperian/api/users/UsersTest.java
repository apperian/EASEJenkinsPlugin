package com.apperian.api.users;

import com.apperian.api.TestCredentials;
import com.apperian.api.TestUtil;
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