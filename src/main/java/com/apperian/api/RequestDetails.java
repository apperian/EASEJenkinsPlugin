package com.apperian.api;

import java.io.File;
import java.util.Map;

public class RequestDetails {

    // By default GET
    private RequestMethod method = RequestMethod.GET;

    private String path = null;

    private Map<String, Object> data = null;

    private String fileField = null;

    private File file = null;

    private RequestDetails() {}

    public static class Builder {

        private RequestDetails requestDetails = new RequestDetails();

        public Builder withMethod(RequestMethod method) {
            requestDetails.method = method;
            return this;
        }

        public Builder withPath(String path, String... arguments) {
            if (arguments.length > 0) {
                requestDetails.path = String.format(path, arguments);
            } else {
                requestDetails.path = path;
            }
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            requestDetails.data = data;
            return this;
        }

        public Builder withFile(String fileField, File file) {
            requestDetails.fileField = fileField;
            requestDetails.file = file;
            return this;
        }

        public RequestDetails build() {
            return requestDetails;
        }
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getFileField() {
        return fileField;
    }

    public File getFile() {
        return file;
    }

}
