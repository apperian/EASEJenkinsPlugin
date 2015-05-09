package com.apperian.eas.metadata;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.apperian.eas.Metadata;
import com.apperian.eas.Metadata.KnownFields;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;

public class AndroidMetadataExtractor extends MetadataExtractor {
    private File file;

    public AndroidMetadataExtractor(File file, PrintStream logger) {
        super(logger);
        this.file = file;
    }

    @Override
    public boolean extractTo(Metadata metadata) {
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

}
