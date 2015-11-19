package com.apperian.api;

import java.util.Map;

public class JsonRpcError {
    int code;
    String message;
    Map<String, Object> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "JsonRpcError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean checkError(int errorCode) {
        return getCode() == errorCode
                && getData() != null
                && getData().containsKey(APIConstants.ERROR_FIELD_DETAILED_MESSAGE);
    }

    public String getDetailedMessage() {
        return (String) getData().get(APIConstants.ERROR_FIELD_DETAILED_MESSAGE);
    }

    public void appendDetailedMessage(String message) {
        Object val = getData().get(APIConstants.ERROR_FIELD_DETAILED_MESSAGE);
        if (val == null) {
            val = "";
        }
        String msg = val.toString();
        msg += message;
        getData().put(APIConstants.ERROR_FIELD_DETAILED_MESSAGE, msg);
    }

    public String getErrorMessage() {
        if (checkError(APIConstants.ERROR_CODE_GENERIC)) {
            return getDetailedMessage();
        } else if (checkError(APIConstants.ERROR_CODE_SESSION_EXPIRED)) {
            return "Session epxired: " + this;
        } else if (checkError(APIConstants.ERROR_CODE_MISSING_PARAMETER)) {
            return "Bad JSON RPC call: " + this;
        } else {
            return "JSON RPC error: " + this;
        }
    }
}
