package com.apperian.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ApperianResourceID {
    final long numValue;

    @JsonCreator
    public ApperianResourceID(long numValue) {
        this.numValue = numValue;
    }

    @JsonValue
    public long getNumericValue() {
        return numValue;
    }

    @Override
    public String toString() {
        return Long.toString(numValue);
    }
}
