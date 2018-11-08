package org.jenkinsci.plugins.apperian;

import com.apperian.api.ApperianApi;

public class ApperianApiFactory {

    public ApperianApi create(String environment, String apperianUrl, String apiToken) {
        ProductionEnvironment productionEnvironment = ProductionEnvironment.fromNameOrNA(environment);
        if (productionEnvironment == ProductionEnvironment.CUSTOM) {
            return new ApperianApi(apperianUrl, apiToken);
        } else {
            return new ApperianApi(productionEnvironment.getApperianUrl(), apiToken);
        }
    }
}
