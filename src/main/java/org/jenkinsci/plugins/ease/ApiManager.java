package org.jenkinsci.plugins.ease;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.JsonHttpEndpoint;
import org.jenkinsci.plugins.api.ApiConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiManager {
    static final Logger logger = Logger.getLogger(ApiManager.class.getName());

    public ApiConnection createConnection(String environment, String apperianUrl, String apiToken) {
        return createEndpoint(environment, apperianUrl, apiToken);
    }

    public boolean isConnectionSuccessful(final ApperianEndpoint endpoint) {
        try {
            endpoint.checkSessionToken();
            return true;
        } catch (Exception e) {
            String message = "Could not authenticate to '" + endpoint.getUrl() + "', error:  " + e.getMessage();
            logger.log(Level.WARNING, message, e);
            endpoint.setLastLoginError(e.getMessage());
        }
        return false;
    }

    public ApiConnection createEndpoint(String environment, String apperianUrl, String apiToken) {
        return new ApiConnection(createApperianEndpoint(environment, apiToken, apperianUrl));
    }

    private ApperianEndpoint createApperianEndpoint(String environment, String sessionToken, String customApperianUrl) {
        ProductionEnvironment productionEnvironment = ProductionEnvironment.fromNameOrNA(environment);
        if (productionEnvironment == ProductionEnvironment.CUSTOM) {
            return new ApperianEndpoint(customApperianUrl, sessionToken);
        } else {
            return new ApperianEndpoint(productionEnvironment.apperianUrl, sessionToken);
        }
    }
}
