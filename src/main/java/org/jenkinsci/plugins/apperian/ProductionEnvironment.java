package org.jenkinsci.plugins.apperian;

public enum ProductionEnvironment {
    NORTH_AMERICA("North America", "https://na01ws.apperian.com"),
    EUROPE("Europe", "https://eu01ws.apperian.eu"),
    CUSTOM("Custom URLs", "https://___.apperian.eu"),;

    public static final ProductionEnvironment DEFAULT_PRODUCTION_ENVIRONMENT = NORTH_AMERICA;

    private String apperianUrl;
    private String title;

    private ProductionEnvironment(String title, String apperianUrl) {
        this.title = title;
        this.apperianUrl = apperianUrl;
    }

    public String getApperianUrl() {
        return apperianUrl;
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
