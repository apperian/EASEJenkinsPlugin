package com.apperian.eas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Joiner;

public class Metadata {
    public interface KnownFields {
        String NAME = "name";
        String AUTHOR = "author";
        String VERSION = "version";
        String VERSION_NOTES = "versionNotes";
        String SHORT_DESCRIPTION = "shortdescription";
        String LONG_DESCRIPTION = "longdescription";

        List<String> ALL_KEYS = Arrays.asList(NAME,
                                              AUTHOR,
                                              VERSION,
                                              VERSION_NOTES,
                                              SHORT_DESCRIPTION,
                                              LONG_DESCRIPTION);
    }

    private final Map<String, String> values;

    @JsonCreator
    public Metadata(Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    @JsonValue
    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        List<String> meta = new ArrayList<>();

        for (String key : KnownFields.ALL_KEYS) {
            addParam(meta, key);
        }

        ArrayList<String> list = new ArrayList<>(values.keySet());
        list.removeAll(KnownFields.ALL_KEYS);

        for (String key : list) {
            addParam(meta, key);
        }

        return "Metadata{" + Joiner.on(", ").join(meta) + '}';
    }

    private void addParam(List<String> meta, String name) {
        String value = values.get(name);

        if (value == null || value.trim().isEmpty()) {
            return;
        }

        meta.add(name + "=" + value);
    }

    public String getShortDescription() {
        return values.get(KnownFields.SHORT_DESCRIPTION);
    }

    public void setShortDescription(String value) {
        putOrRemove(KnownFields.SHORT_DESCRIPTION, value);
    }

    public String getLongDescription() {
        return values.get(KnownFields.LONG_DESCRIPTION);
    }

    public void setLongDescription(String value) {
        putOrRemove(KnownFields.LONG_DESCRIPTION, value);
    }

    public String getName() {
        return values.get(KnownFields.NAME);
    }

    public void setName(String value) {
        putOrRemove(KnownFields.NAME, value);
    }

    public String getAuthor() {
        return values.get(KnownFields.AUTHOR);
    }

    public void setAuthor(String value) {
        putOrRemove(KnownFields.AUTHOR, value);
    }

    public String getVersion() {
        return values.get(KnownFields.VERSION);
    }

    public void setVersion(String value) {
        putOrRemove(KnownFields.VERSION, value);
    }

    public String getVersionNotes() {
        return values.get(KnownFields.VERSION_NOTES);
    }

    public void setVersionNotes(String value) {
        putOrRemove(KnownFields.VERSION_NOTES, value);
    }

    private void putOrRemove(String name, String value) {
        if (value == null) {
            values.remove(name);
        } else {
            values.put(name, value);
        }
    }

}
