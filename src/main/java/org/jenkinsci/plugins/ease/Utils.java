package org.jenkinsci.plugins.ease;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

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
}
