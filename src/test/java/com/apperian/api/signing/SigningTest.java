package com.apperian.api.signing;

import static org.junit.Assume.assumeTrue;

import java.util.List;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SigningTest {

    @Before
    public void beforeMethod() {
        // Skip tests if the properties file has not been configured.
        assumeTrue(ApiTesting.PROPERTIES_FILE_EXISTS);
    }

    @Test
    public void testListCredentials() throws Exception {
        ApperianApi apperianApi = ApiTesting.getApperianApi();
        List<SigningCredential> credentials = apperianApi.listCredentials();

        Assert.assertNotNull(credentials);
    }

    @Test
    public void testSignApplication() throws Exception {
        ApperianApi apperianApi = ApiTesting.getApperianApi();
        SignApplicationResponse response = apperianApi.signApplication(ApiTesting.ANDROID_CREDENTIALS_ID, ApiTesting.ANDROID_APP_ID);

        Assert.assertNotNull(response.getStatus());
    }

}
