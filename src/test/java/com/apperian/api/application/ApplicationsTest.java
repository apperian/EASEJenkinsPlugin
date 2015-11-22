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

        ApplicationListResponse response = ApperianEaseApi.APPLICATIONS.list()
                .call(ApiTesting.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);
        Assert.assertNotNull(response.getApplications());
    }

    @Test
    public void testEnableApp() throws Exception {
        if (TestUtil.shouldSkipIntegrationTests()) {
            return;
        }

        UpdateApplicationMetadataResponse response;
        response = ApperianEaseApi.APPLICATIONS.updateApplicationMetadata(ApiTesting.APP_PSK)
                .setEnabled(true)
                .call(ApiTesting.APERIAN_ENDPOINT);

        TestUtil.assertNoError(response);
        Assert.assertTrue(response.updateResult);
    }
}