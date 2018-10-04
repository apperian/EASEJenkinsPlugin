package com.apperian.api.applications;

import static org.junit.Assume.assumeTrue;

import java.util.List;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationsTest {

    @Before
    public void beforeMethod() {
        // Skip tests if the properties file has not been configured.
        assumeTrue(ApiTesting.PROPERTIES_FILE_EXISTS);
    }

    @Test
    public void testListApps() throws Exception {
        ApperianApi apperianApi = ApiTesting.getApperianApi();

        List<Application> apps = apperianApi.listApplications();

        Assert.assertNotNull(apps);
    }

    @Test
    public void testEnableApp() throws Exception {
        ApperianApi apperianApi = ApiTesting.getApperianApi();
        Application app = apperianApi.updateApplication(ApiTesting.ANDROID_APP_ID, true);

        Assert.assertEquals(app.getId(), ApiTesting.ANDROID_APP_ID);
    }
}