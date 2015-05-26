package com.apperian.eas.metadata;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.apperian.eas.Metadata;
import com.apperian.eas.Metadata.KnownFields;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;

public class AndroidMetadataExtractor extends MetadataExtractor {
    public AndroidMetadataExtractor() {
    }

    @Override
    public boolean tryExtractTo(Metadata metadata, File file, PrintStream logger) {
        this.jenkinsLogger = logger;
        try {
            try(ApkParser apkParser = new ApkParser(file)) {
                extractApkMeta(metadata, apkParser);
            }
            return true;
        } catch (IOException e) {
            report("Problem with reading apk '%s'", e.getMessage());
            return false;
        }
    }

    private void extractApkMeta(Metadata metadata, ApkParser apkParser) throws IOException {
        ApkMeta apkMeta = apkParser.getApkMeta();

        setAndLog(metadata, KnownFields.NAME, apkMeta.getLabel());
        setAndLog(metadata, KnownFields.VERSION, apkMeta.getVersionName());
    }

    @Override
    protected boolean checkFileAcceptable(File file) {
        if (file.getName().endsWith(".apk")) {
            scoreValue += 5;
        }
        return true;
    }
}
