package com.apperian.api.publishing;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianEase;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class PublishingTest {
    @Test
    public void testAuthenticateUser() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApplicationListResponse response = ApperianEase.PUBLISHING.list()
                .call(ApiTesting.EASE_ENDPOINT);

        TestUtil.assertNoError(response);
    }
}
