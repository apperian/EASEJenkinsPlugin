package org.jenkinsci.plugins.ease;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.JsonHttpEndpoint;
import com.apperian.api.EASEEndpoint;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.api.ApperianEaseEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiManager {
    static final Logger logger = Logger.getLogger(ApiManager.class.getName());

    private CredentialsManager credentialsManager = new CredentialsManager();

    public ApperianEaseEndpoint createConnection(EaseUpload upload, boolean ease, boolean apperian) throws ConnectionException {
        ApiManager apiManager = new ApiManager();
        ApperianEaseEndpoint endpoint = createEndpoint(upload);

        if (ease) {
            EASEEndpoint easeEndpoint = endpoint.getEaseEndpoint();
            if (!apiManager.isConnectionSuccessful(easeEndpoint)) {
                throw new ConnectionException("The connection with EASE is not correct");
            }
        }
        if (apperian) {
            ApperianEndpoint apperianEndpoint = endpoint.getApperianEndpoint();
            if (!apiManager.isConnectionSuccessful(apperianEndpoint)) {
                throw new ConnectionException("The connection with Apperian is not correct");
            }
        }

        return endpoint;
    }

    public boolean isConnectionSuccessful(final JsonHttpEndpoint endpoint) {
        try {
            endpoint.checkSessionToken();
            return true;
        } catch (Exception e) {
            String message = "Could not authenticate to '" + endpoint.getUrl() +
                    ", error='" + e.getMessage();
            logger.log(Level.WARNING, message, e);
            endpoint.setLastLoginError(e.getMessage());
        }
        return false;
    }

    public ApperianEaseEndpoint createEndpoint(EaseUpload upload) {
        String apiToken = credentialsManager.getCredentialWithId(upload.getApiTokenId());
        String environment = upload.getProdEnv();
        String customEaseUrl = upload.getCustomEaseUrl();
        String customApperianUrl = upload.getCustomApperianUrl();

        return new ApperianEaseEndpoint(createEaseEndpoint(environment, apiToken, customEaseUrl),
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
