package org.jenkinsci.plugins.ease;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

public class Utils {
    public static String trim(String url) {
        return url == null ? "" : url.trim();
    }

    public static String override(String v1, String v2) {
        return !v1.isEmpty() ? v1 : v2;
    }

    public static boolean isEmptyString(String val) {
        return val.isEmpty();
    }

    public static Map<String, String> parseMetadataAssignment(String strDef) {
        String trimmed = trim(strDef);
        if (trimmed.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return Splitter.on(";")
                       .withKeyValueSeparator("=")
                       .split(trimmed);

    }

    public static Map<String, String> parseAssignmentMap(String str) {
        str = trim(str);
        if (str.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return Splitter.on(";")
                       .trimResults()
                       .withKeyValueSeparator("=")
                       .split(str);
    }

    public static String outAssignmentMap(Map<String, String> map) {
        return Joiner.on(";")
                     .withKeyValueSeparator("=")
                     .join(map);
    }

    public static HttpClientBuilder configureProxy(HttpClientBuilder builder) {
        ProxyConfiguration proxyConfig = null;
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
            proxyConfig = jenkins.proxy;
        }
        if (proxyConfig == null) {
            builder.useSystemProperties();
            return builder;
        }

        Proxy proxy = proxyConfig.createProxy(null);
        if (proxy == null || proxy.type() != Type.HTTP) {
            return builder;
        }

        SocketAddress addr = proxy.address();
        if (addr == null || !(addr instanceof InetSocketAddress)) {
            return builder;
        }

        InetSocketAddress proxyAddr = (InetSocketAddress) addr;
        HttpHost proxyHost = new HttpHost(proxyAddr.getHostString(),
                                          proxyAddr.getPort());

        builder.setProxy(proxyHost);

        String proxyUser = proxyConfig.getUserName();
        if (proxyUser == null) {
            return builder;
        }

        String proxyPass = proxyConfig.getPassword();
        CredentialsProvider cred = new BasicCredentialsProvider();
        cred.setCredentials(new AuthScope(proxyHost),
                            new UsernamePasswordCredentials(proxyUser, proxyPass));
        builder
                .setDefaultCredentialsProvider(cred)
                .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

        return builder;
    }
}
