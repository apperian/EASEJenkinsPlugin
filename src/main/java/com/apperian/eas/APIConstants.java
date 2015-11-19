package com.apperian.eas;

import org.apache.http.Header;

import java.util.concurrent.atomic.AtomicLong;

public interface APIConstants {
    String AUTHENTICATE_USER_METHOD = "com.apperian.eas.user.authenticateuser";
    String GET_LIST_METHOD = "com.apperian.eas.apps.getlist";
    String UPDATE_METHOD = "com.apperian.eas.apps.update";
    String PUBLISH_METHOD = "com.apperian.eas.apps.publish";

    int ERROR_CODE_GENERIC = 1;
    int ERROR_CODE_SESSION_EXPIRED = 666;
    int ERROR_CODE_MISSING_PARAMETER = -32602;

    String JSON_RPC_VERSION = "2.0";
    String API_VERSION = "1.0";
    String REQUEST_CHARSET = "UTF-8";

    AtomicLong ID_GENERATOR = new AtomicLong();
    String ERROR_FIELD_DETAILED_MESSAGE = "detailedMessage";

    String X_TOKEN_HEADER = "X-TOKEN";
}
