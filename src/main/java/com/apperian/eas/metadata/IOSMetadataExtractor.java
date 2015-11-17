package com.apperian.eas.metadata;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
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
        boolean extracted = false;
        try (ZipFile zip = new ZipFile(file)) {
        	// try iTunesMetadata.plist first        	
        	extracted = extractiTunesMetadata(zip, metadata);
        	if (!extracted) {
        		// try Info.plist        		
        		extracted = extractInfoMetadata(zip, metadata);
        	}
        	if (!extracted) {
        		this.jenkinsLogger.println("Unable to find metadata inside the app");
        	}
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return extracted;
    }
    
    private ZipEntry findFileinZip(String name, ZipFile file)
    {
    	for (Enumeration<? extends ZipEntry> e = file.entries();  e.hasMoreElements();) {
    		ZipEntry zipEntry = e.nextElement();
	        if (zipEntry.getName().endsWith(name))
	          return zipEntry;
    	}
      return null;
    }

    private boolean extractInfoMetadata(ZipFile zip, Metadata metadata) {
    	ZipEntry entry = findFileinZip("Info.plist", zip);
        if (entry == null) {
            return false;
        }
        this.jenkinsLogger.println("Found Info.plist in iOS app");        
        try (InputStream in = zip.getInputStream(entry)) {
            NSDictionary dict = (NSDictionary) PropertyListParser.parse(in);

            String bundleDisplayName = getCfg(dict, "CFBundleDisplayName","CFBundleName");            
            String bundleVersion = getCfg(dict, "CFBundleVersion");

            setAndLog(metadata, KnownFields.NAME, bundleDisplayName);            
            setAndLog(metadata, KnownFields.VERSION, bundleVersion);
        } catch (Exception ex) {
        	this.jenkinsLogger.println(ex.toString());
            ex.printStackTrace();
            return false;
        }
        return true;
   }

    
    private boolean extractiTunesMetadata(ZipFile zip, Metadata metadata) {
    	 ZipEntry entry = findFileinZip("iTunesMetadata.plist", zip);    	 
         if (entry == null) {
             return false;
         }         
         this.jenkinsLogger.println("Found iTunesMetadata.plist in iOS app");
         try (InputStream in = zip.getInputStream(entry)) {
             NSDictionary dict = (NSDictionary) PropertyListParser.parse(in);

             String bundleDisplayName = getCfg(dict, "itemName", "bundleDisplayName", "playlistName");
             String artistName = getCfg(dict, "artistName");
             String bundleVersion = getCfg(dict, "bundleVersion");

             setAndLog(metadata, KnownFields.NAME, bundleDisplayName);
             setAndLog(metadata, KnownFields.AUTHOR, artistName);
             setAndLog(metadata, KnownFields.VERSION, bundleVersion);
         } catch (Exception ex) {
             ex.printStackTrace();
             return false;
         }
    	return true;
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
