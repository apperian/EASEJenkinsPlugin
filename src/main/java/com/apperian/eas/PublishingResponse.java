package com.apperian.eas;

import java.util.Map;

public class PublishingResponse {
    long id;
    String jsonrpc;
    String apiVersion;
    JsonRpcError error;

    public long getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public JsonRpcError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public String getErrorMessage() {
        if (error == null) {
            return null;
        }

        if (error.checkError(APIConstants.ERROR_CODE_GENERIC)) {
            return error.getDetailedMessage();
        } else if (error.checkError(APIConstants.ERROR_CODE_SESSION_EXPIRED)) {
            return "Session epxired: " + error;
        } else if (error.checkError(APIConstants.ERROR_CODE_MISSING_PARAMETER)) {
            return "Bad JSON RPC call: " + error;
        } else {
            return "JSON RPC error: " + error;
        }
    }

    public static class JsonRpcError {
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
    }

    public void appendError(String message) {
        if (error != null) {
            error.appendDetailedMessage(message);
        }
    }

}
