package org.jenkinsci.plugins.ease;

import hudson.util.Secret;

public class EaseUser {
    private final String username;
    private final Secret password;
    private final String description;

    public EaseUser(String username, Secret password, String description) {
        this.username = username;
        this.password = password;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public Secret getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }
}
