package org.jenkinsci.plugins.ease;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.apperian.api.ApiTesting;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import hudson.model.BuildListener;

public class UploadTest {

    private static final String FOO_API_KEY_ID = "foo_api_key_id";

    @Before
    public void beforeMethod() {
        // Skip tests if the properties file has not been configured.
        assumeTrue(ApiTesting.PROPERTIES_FILE_EXISTS);
    }

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Test
    public void testAndroid() throws Exception {
        upload(ApiTesting.ANDROID_APP_ID, "android.apk");
    }

    private void upload(String appId, String filename) throws IOException, InterruptedException, URISyntaxException {
        EaseUpload upload = new EaseUpload.Builder("CUSTOM", ApiTesting.APPERIAN_API_URL, FOO_API_KEY_ID)
            .withAppId(appId)
            .withEnableApp(true)
            .withSignApp(true)
            .withFilename(filename)
            .withVersion("1.0.1")
            .withVersionNotes("Built at $BUILD_TIMESTAMP")
            .withCredential(ApiTesting.ANDROID_CREDENTIALS_ID)
            .build();

        File appBinary = new File(getClass().getResource(filename).toURI());
        assertNotNull(appBinary);
        assertTrue(appBinary.exists());

        BuildListener listener = EasyMock.createMock(BuildListener.class);
        EasyMock.expect(listener.getLogger()).andReturn(System.out).anyTimes();
        EasyMock.replay(listener);

        PublishFileCallable callable = new PublishFileCallable(upload,
                                                               listener);


        CredentialsManager credentialsManagerMock = Mockito.mock(CredentialsManager.class);
        Mockito.when(credentialsManagerMock.getCredentialWithId(FOO_API_KEY_ID)).thenReturn(ApiTesting.API_TOKEN);
        callable.setCredentialsManager(credentialsManagerMock);
        Assert.assertTrue("Upload Failed!", callable.invoke(appBinary, null));
    }
}
