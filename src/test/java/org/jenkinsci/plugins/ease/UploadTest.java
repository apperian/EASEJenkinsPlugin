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

import com.apperian.eas.publishing.GetListResponse.Application;
import com.apperian.eas.publishing.Publishing;
import com.apperian.eas.EASEEndpoint;

import hudson.model.BuildListener;

public class UploadTest {
    public static final String URL = "https://easesvc.apperian.eu/ease.interface.php";
    public static final String USER = "oleksiyp@railsreactor.com";
    public static final String PWD = "";

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Test
    public void testListApps() throws Exception {
        if (PWD.isEmpty()) {
            return;
        }

        EASEEndpoint endpoint = new EASEEndpoint(URL);

        String token = Publishing.API.authenticateUser(USER, PWD)
                                    .call(endpoint).result.token;

        Application[] apps = Publishing.API.getList(token)
                                          .call(endpoint).result.applications;

        for (Application app : apps) {
            System.out.println(app.ID);
        }
    }

    @Test
    public void testAndroid() throws Exception {
        if (PWD.isEmpty()) {
            return;
        }

        upload("1EhXFWxikr6erSk1RVHMlw", "android.apk");
    }

    @Test
    public void testIOS() throws Exception {
        if (PWD.isEmpty()) {
            return;
        }

        upload("1EhXFWxikr6erSk1RVHMlw", "ios.ipa");
    }

    @Test
    public void testWinPhoneAppx() throws Exception {
        if (PWD.isEmpty()) {
            return;
        }

        upload("1EhXFWxikr6erSk1RVHMlw", "winphone.appx");
    }

    @Test
    public void testBlackberry() throws Exception {
        if (PWD.isEmpty()) {
            return;
        }

        upload("1EhXFWxikr6erSk1RVHMlw", "blackberry.zip");
    }

    private void upload(String appId, String filename) throws IOException, InterruptedException {
        EaseUpload upload;
        upload = new EaseUpload(URL,
                                USER,
                                PWD,
                                appId,
                                filename,
                                "abc=ghi");

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
        Assert.assertTrue("upload succeeds", callable.invoke(tmpFile, null));
        if (res != null) {
            tmpFile.delete();
        }
    }
}
