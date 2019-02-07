package org.jenkinsci.plugins.apperian;

public class ApiToken {
    final String apiTokenValue;
    final String description;

    public ApiToken(String apiTokenValue, String description) {
        this.apiTokenValue = apiTokenValue;
        this.description = description;
    }

    public String getApiTokenValue() {
        return apiTokenValue;
    }

    public String getDescription() {
        return description;
    }
}
