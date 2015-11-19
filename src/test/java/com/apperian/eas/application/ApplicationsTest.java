package com.apperian.eas.application;

import com.apperian.eas.TestCredentials;
import com.apperian.eas.TestUtil;
import junit.framework.Assert;
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