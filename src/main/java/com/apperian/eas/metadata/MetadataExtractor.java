package com.apperian.eas.metadata;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.apperian.eas.Metadata;

public abstract class MetadataExtractor implements Comparable<MetadataExtractor> {
    protected static final Logger logger = Logger.getLogger(MetadataExtractor.class.getName());

    protected PrintStream jenkinsLogger;
    protected int scoreValue;

    protected MetadataExtractor() {

    }

    public static List<MetadataExtractor> allExtractors(File file) {
        List<MetadataExtractor> extractors = new ArrayList<>();

        addExtractorByClass(extractors, file,
                            "com.apperian.eas.metadata.AndroidMetadataExtractor");
        addExtractorByClass(extractors, file,
                            "com.apperian.eas.metadata.IOSMetadataExtractor");
        addExtractorByClass(extractors, file,
                            "com.apperian.eas.metadata.WinPhoneAppxMetadataExtractor");
        addExtractorByClass(extractors, file,
                            "com.apperian.eas.metadata.BlackberryMetadataExtractor");

        Collections.sort(extractors);

        return extractors;
    }

    private static void addExtractorByClass(List<MetadataExtractor> extractors,
                                            File file,
                                            String clsName) {
        try {
            Class<?> cls = Class.forName(clsName);
            MetadataExtractor extractor = (MetadataExtractor)cls.newInstance();
            if (!extractor.checkFileAcceptable(file)) {
                return;
            }
            extractors.add(extractor);
        } catch (Throwable e) {
            logger.throwing("MetadataExtractor", "addExtractorByClass", e);
        }
    }

    protected boolean checkFileAcceptable(File file) {
        return true;
    }

    public boolean extractTo(Metadata metadata, File file, PrintStream logger) {
        try {
            return tryExtractTo(metadata, file, logger);
        } catch (Throwable e) {
            this.logger.throwing("MetadataExtractor", "addExtractorByClass", e);
            return false;
        }
    }

    protected abstract boolean tryExtractTo(Metadata metadata, File file, PrintStream logger);

    protected void report(String msg, Object ...args) {
        if (jenkinsLogger != null) {
            jenkinsLogger.println(String.format(msg, args));
        }
    }

    protected void setAndLog(Metadata metadata, String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        report("Extracted %s = '%s'", name, value);
        metadata.getValues().put(name, value);
    }

    @Override public int compareTo(MetadataExtractor o) {
        return -Integer.compare(score(), o.score());
    }

    protected int score() {
        return scoreValue;
    }
}
