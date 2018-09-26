package org.jenkinsci.plugins.ease;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.util.Streams;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.BuildListener;

public class UploadTest {
    public static final String URL = "https://easesvc.apperian.eu/ease.interface.php";
    public static final String API_TOKEN = null;

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    // TODO:  Get these working again.  They were not being run before we switched to using the API token.
//    @Test
//    public void testAndroid() throws Exception {
//        upload("1EhXFWxikr6erSk1RVHMlw", "android.apk");
//    }
//
//    @Test
//    public void testIOS() throws Exception {
//        upload("1EhXFWxikr6erSk1RVHMlw", "ios.ipa");
//    }
//
//    @Test
//    public void testWinPhoneAppx() throws Exception {
//        upload("1EhXFWxikr6erSk1RVHMlw", "winphone.appx");
//    }
//
//    @Test
//    public void testBlackberry() throws Exception {
//        upload("1EhXFWxikr6erSk1RVHMlw", "blackberry.zip");
//    }

    private void upload(String appId, String filename) throws IOException, InterruptedException {
        EaseUpload upload = EaseUpload.simpleUpload(
                "EUROPE",
                null,
                API_TOKEN);

        InputStream res = getClass().getResourceAsStream(filename);
        File tmpFile = new File(filename);
        if (res != null) {
            try (FileOutputStream out = new FileOutputStream(tmpFile);
                        InputStream in = res) {
                Streams.copy(in, out, false);
            }
        }

        BuildListener listener = EasyMock.createMock(BuildListener.class);
        EasyMock.expect(listener.getLogger()).andReturn(System.out).anyTimes();
        EasyMock.replay(listener);

        PublishFileCallable callable = new PublishFileCallable(upload,
                                                               listener);
        Assert.assertTrue("Upload Failed!", callable.invoke(tmpFile, null));
        if (res != null) {
            tmpFile.delete();
        }
    }
}
