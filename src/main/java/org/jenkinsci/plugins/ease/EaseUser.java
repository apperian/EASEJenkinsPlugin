package org.jenkinsci.plugins.ease;

public class EaseUser {
    final String username;
    final String password;
    final String description;

    public EaseUser(String username, String password, String description) {
        this.username = username;
        this.password = password;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }
}
