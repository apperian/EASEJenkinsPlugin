package com.apperian.eas;

import com.apperian.eas.publishing.AuthenticateUserRequest;
import com.apperian.eas.publishing.AuthenticateUserResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EASEEndpointTest {

    private EASEEndpoint api;

    @Before
    public void setUp() {
        api = new EASEEndpoint("https://easesvc.apperian.com/ease.interface.php");
    }

    @Test
    @Ignore // for manual running only
    public void testError() throws Exception {
        AuthenticateUserResponse response = new AuthenticateUserRequest("test-easy-jenkins-user", "testpassword")
                .call(api);
        System.out.println(response);
    }
}
