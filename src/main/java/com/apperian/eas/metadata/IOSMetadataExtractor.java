package com.apperian.eas.metadata;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.apperian.eas.Metadata;
import com.apperian.eas.Metadata.KnownFields;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

public class IOSMetadataExtractor extends MetadataExtractor {
    public IOSMetadataExtractor() {
    }

    @Override
    public boolean tryExtractTo(Metadata metadata, File file, PrintStream logger) {
        this.jenkinsLogger = logger;
        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry entry = zip.getEntry("iTunesMetadata.plist");
            if (entry == null) {
                return false;
            }
            try (InputStream in = zip.getInputStream(entry)) {
                NSDictionary dict = (NSDictionary) PropertyListParser.parse(in);

                extractProperties(metadata, dict);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void extractProperties(Metadata metadata, NSDictionary cnf) {
        String bundleDisplayName = getCfg(cnf, "itemName", "bundleDisplayName", "playlistName");
        String artistName = getCfg(cnf, "artistName");
        String bundleVersion = getCfg(cnf, "bundleVersion");

        setAndLog(metadata, KnownFields.NAME, bundleDisplayName);
        setAndLog(metadata, KnownFields.AUTHOR, artistName);
        setAndLog(metadata, KnownFields.VERSION, bundleVersion);
    }

    private String getCfg(NSDictionary cnf, String ...props) {
        for (String prop : props) {
            NSObject val = cnf.get(prop);
            if (val != null) {
                return val.toString();
            }
        }
        return null;
    }

    @Override
    protected boolean checkFileAcceptable(File file) {
        String name = file.getName();
        if (name.endsWith(".app") || name.endsWith(".ipa")) {
            scoreValue += 5;
        }
        return true;
    }
}
