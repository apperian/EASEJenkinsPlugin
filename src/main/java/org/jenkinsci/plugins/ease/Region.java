package org.jenkinsci.plugins.ease;

public enum Region {
    NORTH_AMERICA("North America", "https://na01ws.apperian.com/v1", "https://easesvc.apperian.com/ease.interface.php"),
    EUROPE("Europe", "https://eu01ws.apperian.eu/v1", "https://easesvc.apperian.eu/ease.interface.php"),
    CUSTOM("Custom URLs", "https://___.apperian.eu/v1", "https://easesvc.apperian.__/ease.interface.php"),;

    public static final Region DEFAULT_REGION = NORTH_AMERICA;

    String apperianUrl;
    String easeUrl;
    String title;

    Region(String title, String apperianUrl, String easeUrl) {
        this.title = title;
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

    public String getTitle() {
        return title;
    }
}
