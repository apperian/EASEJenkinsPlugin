package com.apperian.eas.metadata;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.xpath.XPathExpressionException;

import com.apperian.eas.Metadata;
import com.apperian.eas.Metadata.KnownFields;

public class BlackberryMetadataExtractor extends MetadataExtractor {
    public BlackberryMetadataExtractor() {
    }

    @Override
    public boolean extractTo(Metadata metadata, File file, PrintStream logger) {
        this.jenkinsLogger = logger;
        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".alx")) {
                    continue;
                }

                try (InputStream in = zip.getInputStream(entry)) {
                    XMLDoc doc = new XMLDoc(in);
                    extractProperties(metadata, doc);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void extractProperties(Metadata metadata, XMLDoc doc) throws XPathExpressionException {
        String name = doc.extractValue("loader/application/name");
        String versionName = doc.extractValue("loader/application/version");
        String description = doc.extractValue("loader/application/description");

        setAndLog(metadata, KnownFields.NAME, name);
        setAndLog(metadata, KnownFields.VERSION, versionName);
        setAndLog(metadata, KnownFields.SHORT_DESCRIPTION, description);
        setAndLog(metadata, KnownFields.LONG_DESCRIPTION, description);
    }

    @Override
    protected boolean checkFileAcceptable(File file) {
        String name = file.getName();
        if (name.endsWith(".zip") || name.endsWith(".bar")) {
            scoreValue += 5;
        }
        return true;
    }
}
