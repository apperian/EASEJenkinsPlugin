package org.jenkinsci.plugins.ease;

/**
 * Created by oleksiyp on 3/26/14.
 */
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
}
