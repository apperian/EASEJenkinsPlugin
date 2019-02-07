package org.jenkinsci.plugins.apperian;

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
        ApperianUpload upload = new ApperianUpload.Builder("CUSTOM", ApiTesting.APPERIAN_API_URL, ApiTesting.API_TOKEN)
            .withAppId(appId)
            .withEnableApp(true)
            .withSignApp(true)
            .withFilename(filename)
            .withVersion("1.0.1")
            .withVersionNotes("Built for integration tests")
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

        Assert.assertTrue("Upload Failed!", callable.invoke(appBinary, null));
    }
}
