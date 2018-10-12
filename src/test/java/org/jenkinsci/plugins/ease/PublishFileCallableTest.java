package org.jenkinsci.plugins.ease;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import hudson.model.BuildListener;

public class PublishFileCallableTest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    public static final EaseUpload EASE_UPLOAD1 =
            new EaseUpload("NORTH_AMERICA", "url1", "api_token_id",
                    "app1", "filename", "app name", "short description", "long description", "author",  "1.0", "version", true,
                    "cred", false, false);

    @Test
    public void testSerialization() throws Exception {
        BuildListener listener = mock(BuildListener.class,
                                      withSettings().serializable());

        Mockito.when(listener.getLogger()).thenReturn(null);

        PublishFileCallable callable = new PublishFileCallable(EASE_UPLOAD1, listener);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(callable);

        byte[] buf = byteOut.toByteArray();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(buf);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);

        PublishFileCallable deserializedCallable = (PublishFileCallable) objIn.readObject();

        Assert.assertNull(deserializedCallable.getLogger());
        Assert.assertEquals("api_token_id", deserializedCallable.getUpload().getApiTokenId());
        Assert.assertEquals("app1", deserializedCallable.getUpload().getAppId());
        Assert.assertEquals("app name", deserializedCallable.getUpload().getAppName());
        Assert.assertEquals("short description", deserializedCallable.getUpload().getShortDescription());
        Assert.assertEquals("long description", deserializedCallable.getUpload().getLongDescription());
        Assert.assertEquals("author", deserializedCallable.getUpload().getAuthor());
        Assert.assertEquals("version", deserializedCallable.getUpload().getVersionNotes());

    }
}