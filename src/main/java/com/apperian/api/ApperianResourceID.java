package com.apperian.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ApperianResourceID {
    final String id;

    @JsonCreator // input could be a String or Numeric
    public ApperianResourceID(Object id) {
        this.id = id.toString();
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
