package org.jenkinsci.plugins.ease;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import hudson.model.BuildListener;
import hudson.util.Secret;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

public class PublishFileCallableTest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    public static final EaseUpload EASE_UPLOAD1 =
            new EaseUpload("url0", "NORTH_AMERICA", "url1", "url2", "user1", "pass1")
                    .setOtherParams("app1", "filename", "abc=ghi", true, "cred", false);

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
        Assert.assertEquals("user1", deserializedCallable.getUpload().getUsername());
        Assert.assertEquals("pass1", deserializedCallable.getUpload().getPassword());
        Assert.assertEquals("app1", deserializedCallable.getUpload().getAppId());
        Assert.assertEquals(Collections.singletonMap("abc", "ghi"), deserializedCallable.getMetadataAssignment());

    }
}