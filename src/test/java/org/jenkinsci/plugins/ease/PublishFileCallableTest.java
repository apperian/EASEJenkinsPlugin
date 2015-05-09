package org.jenkinsci.plugins.ease;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import hudson.model.BuildListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

public class PublishFileCallableTest {

    public static final EaseUpload EASE_UPLOAD1 = new EaseUpload("url1", "user1", "pass1", "app1", "file1",
                                                                 "");

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
        Assert.assertEquals("url1", deserializedCallable.getUrl());
        Assert.assertEquals("user1", deserializedCallable.getUsername());
        Assert.assertEquals("pass1", deserializedCallable.getPassword());
        Assert.assertEquals("app1", deserializedCallable.getAppId());

    }
}