package org.jenkinsci.plugins.ease;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

public class Utils {
    private static final DateFormat ISO_8601_FORMAT;
    static {
        ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        ISO_8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    public static String trim(String url) {
        return url == null ? "" : url.trim();
    }

    public static String override(String ...args) {
        for (String arg : args) {
            if (!Utils.isEmptyString(arg)) {
                return arg;
            }
        }
        return "";
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isEmptyString(String val) {
        return val == null || val.trim().isEmpty();
    }

    public static synchronized String formatIso8601(Date date) {
        return ISO_8601_FORMAT.format(date);
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

    public static String transformDate(String jsonDateString) {
        int idx = jsonDateString.indexOf('T');
        if (idx != -1) {
            return jsonDateString.substring(0, idx);
        } else {
            return jsonDateString;
        }
    }
}
