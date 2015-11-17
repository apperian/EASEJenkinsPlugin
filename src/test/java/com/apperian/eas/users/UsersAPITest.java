package com.apperian.eas.users;

import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.TestCredentials;
import org.junit.Test;

public class UsersAPITest {
    private AperianEndpoint endpoint = new AperianEndpoint(TestCredentials.APERIAN_ENDPOINT);

    @Test
    public void testAuthenticateUser() throws Exception {
        if (!TestCredentials.areSet()) {
            return;

        }
        AuthenticateUserResponse authResponse =
                UsersAPI.authenticateUser(TestCredentials.USER_ID, TestCredentials.PASSWORD)
                        .call(endpoint);



    }
}