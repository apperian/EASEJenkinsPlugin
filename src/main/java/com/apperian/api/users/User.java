package com.apperian.api.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("id")
    private String id;

    public String getId() {
        return id;
    }
}