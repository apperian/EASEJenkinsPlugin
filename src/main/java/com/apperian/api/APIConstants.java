package com.apperian.api;

public class APIConstants {

    public static final String REQUEST_CHARSET = "UTF-8";

    public static final String X_TOKEN_HEADER = "X-TOKEN";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String JSON_CONTENT_TYPE = "application/json";

    public static final String LIST_APPS_URL_PATH = "/v2/applications";

    public static final String UPDATE_APP_URL_PATH = "/v1/applications/%s";

    public static final String GET_APP_URL_PATH = "/v2/applications/%s";

    public static final String GET_CREDENTIALS_URL_PATH = "/v1/credentials";

    public static final String SIGN_APP_URL_PATH = "/v1/applications/%s/credentials/%s";

    public static final String GET_USER_INFO_URL_PATH = "/v2/users/info";
}
