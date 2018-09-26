package com.apperian.api.application;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;
import com.apperian.api.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationsTest {

    private ApperianApi apperianApi = new ApperianApi();

    @Test
    public void testListApps() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }



        ApplicationListResponse response = apperianApi.listApplications(ApiTesting.getApperianEndpoint());

        Assert.assertNotNull(response.getApplications());
    }

    @Test
    public void testEnableApp() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        Application app = apperianApi.updateApplication(ApiTesting.getApperianEndpoint(), ApiTesting.APP_ID, true);

        Assert.assertEquals(app.getId(), ApiTesting.APP_ID);
    }
}