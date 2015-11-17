package com.apperian.eas.publishing;

public class UploadResult {
    public String fileID;
    public String errorMessage;

    public boolean hasError() {
        return errorMessage != null;
    }
}
