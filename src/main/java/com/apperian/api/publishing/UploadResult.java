package com.apperian.api.publishing;

public class UploadResult {
    public String fileID;
    public String errorMessage;

    public boolean hasError() {
        return errorMessage != null;
    }
}
