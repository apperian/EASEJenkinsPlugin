package org.jenkinsci.plugins.ease;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.JsonHttpEndpoint;
import com.apperian.api.EASEEndpoint;
import org.jenkinsci.plugins.api.ApiConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiManager {
    static final Logger logger = Logger.getLogger(ApiManager.class.getName());

    public ApiConnection createConnection(String environment, String easeUrl, String apperianUrl, String apiToken) {
        return createEndpoint(environment, easeUrl, apperianUrl, apiToken);
    }

    public boolean isConnectionSuccessful(final JsonHttpEndpoint endpoint) {
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

    public ApiConnection createEndpoint(String environment, String easeUrl, String apperianUrl, String apiToken) {
        return new ApiConnection(createEaseEndpoint(environment, apiToken, easeUrl),
                                 createApperianEndpoint(environment, apiToken, apperianUrl));
    }

    private EASEEndpoint createEaseEndpoint(String environment, String sessionToken, String customEaseUrl) {
        ProductionEnvironment productionEnvironment = ProductionEnvironment.fromNameOrNA(environment);
        if (productionEnvironment == ProductionEnvironment.CUSTOM) {
            return new EASEEndpoint(customEaseUrl, sessionToken);
        } else {
            return new EASEEndpoint(productionEnvironment.easeUrl, sessionToken);
        }
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
