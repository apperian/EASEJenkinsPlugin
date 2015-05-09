package com.apperian.eas.metadata;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import com.apperian.eas.Metadata;

import static java.util.Arrays.asList;

public abstract class MetadataExtractor {
    private String errorMessage;
    private PrintStream logger;

    protected MetadataExtractor(PrintStream logger) {
        this.logger = logger;
    }

    public static List<MetadataExtractor> ofFile(File file, PrintStream logger) {
        String name = file.getName();
        if (name.endsWith(".apk")) {
            return asList(android(file, logger),
                          ios(file, logger));
        } else if (name.endsWith(".ipa") || name.endsWith(".app")) {
            return asList(ios(file, logger),
                          android(file, logger));
        }

        return asList(android(file, logger),
                      ios(file, logger));
    }

    public static MetadataExtractor ios(File file, PrintStream logger) {
        return new IOSMetadataExtractor(file, logger);
    }

    public static MetadataExtractor android(File file, PrintStream logger) {
        return new AndroidMetadataExtractor(file, logger);
    }

    public abstract boolean extractTo(Metadata metadata);

    protected void report(String msg, Object ...args) {
        if (logger != null) {
            logger.println(String.format(msg, args));
        }
    }

    protected void setAndLog(Metadata metadata, String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        report("Extracted %s = '%s'", name, value);
        metadata.getValues().put(name, value);
    }
}
