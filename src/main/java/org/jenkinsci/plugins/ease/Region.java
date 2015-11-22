package org.jenkinsci.plugins.ease;

public enum Region {
    NORTH_AMERICA("https://na01ws.apperian.com/v1", "https://easesvc.apperian.com/ease.interface.php"),
    EUROPE("https://eu01ws.apperian.eu/v1", "https://easesvc.apperian.eu/ease.interface.php"),
    CUSTOM("https://___.apperian.eu/v1", "https://easesvc.apperian.__/ease.interface.php"),;

    String apperianUrl;
    String easeUrl;

    Region(String apperianUrl, String easeUrl) {
        this.apperianUrl = apperianUrl;
        this.easeUrl = easeUrl;
    }

    public String getApperianUrl() {
        return apperianUrl;
    }

    public String getEaseUrl() {
        return easeUrl;
    }

    public static Region fromNameOrCustom(String accountRegion) {
        for (Region region : Region.values()) {
            if (region.name().equals(accountRegion)) {
                return region;
            }
        }
        return CUSTOM;
    }
}
