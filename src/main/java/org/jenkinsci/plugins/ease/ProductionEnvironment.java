package org.jenkinsci.plugins.ease;

public enum ProductionEnvironment {
    NORTH_AMERICA("North America", "https://na01ws.apperian.com", "https://easesvc.apperian.com/ease.interface.php"),
    EUROPE("Europe", "https://eu01ws.apperian.eu", "https://easesvc.apperian.eu/ease.interface.php"),
    CUSTOM("Custom URLs", "https://___.apperian.eu", "https://easesvc.apperian.__/ease.interface.php"),;

    public static final ProductionEnvironment DEFAULT_PRODUCTION_ENVIRONMENT = NORTH_AMERICA;

    String apperianUrl;
    String easeUrl;
    String title;

    ProductionEnvironment(String title, String apperianUrl, String easeUrl) {
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

    public static ProductionEnvironment fromNameOrNA(String accountRegion) {
        for (ProductionEnvironment productionEnvironment : ProductionEnvironment.values()) {
            if (productionEnvironment.name().equals(accountRegion)) {
                return productionEnvironment;
            }
        }
        return NORTH_AMERICA;
    }

    public String getTitle() {
        return title;
    }
}
