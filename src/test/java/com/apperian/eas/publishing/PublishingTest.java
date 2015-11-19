package com.apperian.eas.publishing;

import com.apperian.eas.TestCredentials;
import com.apperian.eas.TestUtil;
import org.junit.Assert;
import org.junit.Before;
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
