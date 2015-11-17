package com.apperian.eas.publishing;

import com.apperian.eas.EASEResponse;

import java.util.Arrays;

public class GetListResponse extends EASEResponse {
    public Result result;

    public static class Result {
        public Application[] applications;
    }

    public static class Application {
        public String ID;
        public String author;
        public String bundleId;
        public String longdescription;
        public String name;
        public String shortdescription;
        public String status;
        public String type;
        public String version;
        public String versionNotes;

        @Override
        public String toString() {
            return "Application{" +
                    "ID='" + ID + '\'' +
                    ", bundleId='" + bundleId + '\'' +
                    ", status='" + status + '\'' +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", longdescription='" + longdescription + '\'' +
                    ", version='" + version + '\'' +
                    ", versionNotes='" + versionNotes + '\'' +
                    ", shortdescription='" + shortdescription + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetListResponse{" +
                "applications=" + Arrays.toString(result.applications) +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }
}