package org.jenkinsci.plugins.ease;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.JsonHttpEndpoint;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.ConnectionException;
import org.jenkinsci.plugins.api.ApiConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiManager {
    static final Logger logger = Logger.getLogger(ApiManager.class.getName());

    private CredentialsManager credentialsManager = new CredentialsManager();

    public ApiConnection createConnection(EaseUpload upload) throws ConnectionException {
        return createEndpoint(upload);
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

    public ApiConnection createEndpoint(EaseUpload upload) {
        String apiToken = credentialsManager.getCredentialWithId(upload.getApiTokenId());
        String environment = upload.getProdEnv();
        String customEaseUrl = upload.getCustomEaseUrl();
        String customApperianUrl = upload.getCustomApperianUrl();

        return new ApiConnection(createEaseEndpoint(environment, apiToken, customEaseUrl),
                                 createApperianEndpoint(environment, apiToken, customApperianUrl));
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
