package com.apperian.api.users;

import static org.junit.Assume.assumeTrue;

import com.apperian.api.ApiTesting;
import com.apperian.api.ApperianApi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UsersTest {

    @Before
    public void beforeMethod() {
        // Skip tests if the properties file has not been configured.
        assumeTrue(ApiTesting.PROPERTIES_FILE_EXISTS);
    }

    @Test
    public void testGetUserDetails() throws Exception {
        ApperianApi apperianApi = ApiTesting.getApperianApi();
        User user = apperianApi.getUserDetails();

        Assert.assertEquals(ApiTesting.USER_ID, user.getId());
    }
}
