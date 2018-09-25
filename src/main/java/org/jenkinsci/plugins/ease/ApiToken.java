package org.jenkinsci.plugins.ease;

public class ApiToken {
    final String apiTokenId;
    final String description;

    public ApiToken(String apiTokenId, String description) {
        this.apiTokenId = apiTokenId;
        this.description = description;
    }

    public String getApiTokenId() {
        return apiTokenId;
    }

    public String getDescription() {
        return description;
    }
}
