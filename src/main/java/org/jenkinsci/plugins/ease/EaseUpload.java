package org.jenkinsci.plugins.ease;

import hudson.FilePath;
import org.kohsuke.stapler.DataBoundConstructor;

public class EaseUpload {
    private String url;
    private String appId;
    private String filename;
    private String username;
    private String password;
    private transient  FilePath filePath;

    @DataBoundConstructor
    public EaseUpload(String _url, String _username, String _password, String _appId, String _filename) {
        this.url = trim(_url);
        this.username = trim(_username);
        this.password = trim(_password);
        this.appId = trim(_appId);
        this.filename = trim(_filename);
    }


    public EaseUpload derive(EaseUpload additionalUpload) {
        return new EaseUpload(
                override(additionalUpload.url, url),
                override(additionalUpload.username, username),
                override(additionalUpload.password, password),
                additionalUpload.getAppId(),
                additionalUpload.getFilename());
    }

    public void setFilePath(FilePath filePath) {
        this.filePath = filePath;
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public String getUrl() {
        return url;
    }

    public String getAppId() {
        return appId;
    }

    public String getFilename() {
        return filename;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private static String trim(String url) {
        return url == null ? "" : url.trim();
    }
    private static String override(String v1, String v2) {
        return !v1.isEmpty() ? v1 : v2;
    }
    private static boolean isEmptyString(String val) {
        return val.isEmpty();
    }

    public boolean checkOk() {
        return !(isEmptyString(appId)
                || isEmptyString(url)
                || isEmptyString(username)
                || isEmptyString(password)
                || isEmptyString(filename));
    }
}
