package com.apperian.eas.metadata;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.xpath.XPathExpressionException;

import com.apperian.eas.publishing.Metadata;
import com.apperian.eas.publishing.Metadata.KnownFields;

public class WinPhoneAppxMetadataExtractor extends MetadataExtractor {
    public WinPhoneAppxMetadataExtractor() {
    }

    @Override
    public boolean tryExtractTo(Metadata metadata, File file, PrintStream logger) {
        this.jenkinsLogger = logger;
        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry entry = zip.getEntry("AppxManifest.xml");
            if (entry == null) {
                return false;
            }
            try (InputStream in = zip.getInputStream(entry)) {
                XMLDoc doc = new XMLDoc(in);
                extractProperties(metadata, doc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void extractProperties(Metadata metadata, XMLDoc doc) throws XPathExpressionException {
        String name = doc.extractValue("Package/Properties/DisplayName");
        String author = doc.extractValue("Package/Properties/PublisherDisplayName");
        String versionName = doc.extractValue("Package/Identity/@Version");
        String description = doc.extractValue("Package/Applications/Application[1]/VisualElements/@Description");

        setAndLog(metadata, KnownFields.NAME, name);
        setAndLog(metadata, KnownFields.AUTHOR, author);
        setAndLog(metadata, KnownFields.VERSION, versionName);
        setAndLog(metadata, KnownFields.SHORT_DESCRIPTION, description);
        setAndLog(metadata, KnownFields.LONG_DESCRIPTION, description);
    }

    @Override
    protected boolean checkFileAcceptable(File file) {
        String name = file.getName();
        if (name.endsWith(".appx")) {
            scoreValue += 5;
        }
        return true;
    }
}
