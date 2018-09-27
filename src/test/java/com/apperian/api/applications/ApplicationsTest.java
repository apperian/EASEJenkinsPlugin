package com.apperian.api.applications;

import java.util.List;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;
import com.apperian.api.TestUtil;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationsTest {

    @Test
    public void testListApps() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        ApperianApi apperianApi = ApiTesting.getApperianApi();

        List<Application> apps = apperianApi.listApplications();

        Assert.assertNotNull(apps);
    }

    @Test
    public void testEnableApp() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }
        ApperianApi apperianApi = ApiTesting.getApperianApi();
        Application app = apperianApi.updateApplication(ApiTesting.APP_ID, true);

        Assert.assertEquals(app.getId(), ApiTesting.APP_ID);
    }
}