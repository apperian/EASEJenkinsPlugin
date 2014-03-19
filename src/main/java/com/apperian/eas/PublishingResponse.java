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
    }
}
