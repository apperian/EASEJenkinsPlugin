package com.apperian.eas.users;

import com.apperian.eas.TestCredentials;
import com.apperian.eas.signing.ListAllSigningCredentialsResponse;
import com.apperian.eas.signing.SigningAPI;
import org.junit.Test;

public class SigningAPITest {

    @Test
    public void testListCredentials() throws Exception {
        ListAllSigningCredentialsResponse response;
        response = SigningAPI.listAllSigningCredentials(UsersAPITest.lazyAuth())
                .call(TestCredentials.APERIAN_ENDPOINT);

        System.out.println(response);
    }
}
