package com.apperian.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ApperianResourceID {
    final String id;

    @JsonCreator
    public ApperianResourceID(String id) {
        this.id = id;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
