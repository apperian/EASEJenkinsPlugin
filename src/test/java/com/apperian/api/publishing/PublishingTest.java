package com.apperian.api.publishing;

import com.apperian.api.TestCredentials;
import com.apperian.api.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class PublishingTest {

    @Test
    public void testAuthenticateUser() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        Assert.assertNotNull(TestCredentials.EASE_ENDPOINT.getSessionToken());
    }
}
