package org.jenkinsci.plugins.ease;

public class EaseUser {
    final String apiToken;
    final String description;

    public EaseUser(String apiToken, String description) {
        this.apiToken = apiToken;
        this.description = description;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getDescription() {
        return description;
    }
}
