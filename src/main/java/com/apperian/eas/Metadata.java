package com.apperian.eas;

public class Metadata {
    public String shortdescription;
    public String longdescription;
    public String name;
    public String author;
    public String version;
    public String versionNotes;

    @Override
    public String toString() {
        return "Metadata{" +
                "shortdescription='" + shortdescription + '\'' +
                ", longdescription='" + longdescription + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                ", versionNotes='" + versionNotes + '\'' +
                '}';
    }
}
