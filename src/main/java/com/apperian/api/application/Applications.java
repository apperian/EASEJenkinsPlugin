package com.apperian.api.application;

/**
 * API described at:
 * https://help.apperian.com/display/pub/Applications+API
 */
public class Applications {
    public static Applications API = new Applications();

    public ApplicationListRequest list() {
        return new ApplicationListRequest();
    }
}
