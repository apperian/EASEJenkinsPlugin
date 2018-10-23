package com.apperian.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiTesting {
    private static final String PROPERTIES_FILE_NAME = "com/apperian/api/test-configuration.properties.test";

    public static final boolean PROPERTIES_FILE_EXISTS;
    public static final String API_TOKEN;
    public static final String ANDROID_CREDENTIALS_ID;
    public static final String ANDROID_APP_ID;
    public static final String APPERIAN_API_URL;
    public static final String USER_ID;

    static {
        // Configure testing values from the properties file (if found).
        String apiToken = null;
        String credentialsId = null;
        String appId = null;
        String apiUrl = null;
        String userId = null;

        InputStream stream = ApiTesting.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
        if (stream == null) {
            PROPERTIES_FILE_EXISTS = false;
        } else {
            PROPERTIES_FILE_EXISTS = true;
            Properties properties = new Properties();
            try {
                properties.load(stream);
                apiToken = properties.getProperty("API_TOKEN");
                credentialsId = properties.getProperty("ANDROID_CREDENTIALS_ID");
                appId = properties.getProperty("ANDROID_APP_ID");
                apiUrl = properties.getProperty("APPERIAN_API_URL");
                userId = properties.getProperty("USER_ID");
            } catch (IOException e) {
                throw new RuntimeException("Error parsing test-configuration.properties", e);
            }
        }

        API_TOKEN = apiToken;
        ANDROID_CREDENTIALS_ID = credentialsId;
        ANDROID_APP_ID = appId;
        APPERIAN_API_URL = apiUrl;
        USER_ID = userId;
    }

    public static ApperianApi getApperianApi() {
        return new ApperianApi(APPERIAN_API_URL, API_TOKEN);
    }
}
