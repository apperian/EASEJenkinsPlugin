package com.apperian.api.application;

import com.apperian.api.TestCredentials;
import com.apperian.api.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationsTest {
    @Test
    public void testListApps() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApplicationListResponse response = Applications.API.list()
                .call(TestCredentials.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);
        Assert.assertNotNull(response.getApplications());
    }

}