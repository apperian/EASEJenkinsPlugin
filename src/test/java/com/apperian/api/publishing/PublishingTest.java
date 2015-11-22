package com.apperian.api.publishing;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianEaseApi;
import com.apperian.api.TestUtil;
import org.junit.Test;

public class PublishingTest {
    @Test
    public void testAuthenticateUser() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApplicationListResponse response = ApperianEaseApi.PUBLISHING.list()
                .call(ApiTesting.EASE_ENDPOINT);

        TestUtil.assertNoError(response);
    }
}
