package com.apperian.api.application;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianEaseApi;
import com.apperian.api.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationsTest {
    @Test
    public void testListApps() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApplicationListResponse response = ApperianEaseApi.APPLICATIONS.list(ApiTesting.getApperianEndpoint());

        Assert.assertNotNull(response.getApplications());
    }

    @Test
    public void testEnableApp() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        UpdateApplicationResponse response;
        response = ApperianEaseApi.APPLICATIONS.updateApplication(ApiTesting.getApperianEndpoint(), ApiTesting.APP_ID, true);

        Assert.assertEquals(response.application.getId(), ApiTesting.APP_ID);
    }
}