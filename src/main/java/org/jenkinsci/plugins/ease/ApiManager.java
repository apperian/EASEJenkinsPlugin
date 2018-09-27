package org.jenkinsci.plugins.ease;

import com.apperian.api.ApperianApi;

public class ApiManager {

    public ApperianApi createConnection(String environment, String apperianUrl, String apiToken) {
        ProductionEnvironment productionEnvironment = ProductionEnvironment.fromNameOrNA(environment);
        if (productionEnvironment == ProductionEnvironment.CUSTOM) {
            return new ApperianApi(apperianUrl, apiToken);
        } else {
            return new ApperianApi(productionEnvironment.apperianUrl, apiToken);
        }
    }
}
