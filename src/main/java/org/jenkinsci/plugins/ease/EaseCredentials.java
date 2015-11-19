package org.jenkinsci.plugins.ease;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apperian.eas.publishing.AuthenticateUserResponse;
import com.apperian.eas.EASEEndpoint;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.HostnameRequirement;

import hudson.security.ACL;
import hudson.util.Secret;
import jenkins.model.Jenkins;

public class EaseCredentials {
    private final String url;
    private final List<EaseUser> credentials;

    private static final Logger logger = Logger.getLogger(EaseCredentials.class.getName());

    public EaseCredentials(String url, String username, Secret password) {
        this.url = Utils.trim(url);
        credentials = new ArrayList<>();
        if (!Utils.isEmptyString(username)) {
            credentials.add(new EaseUser(
                    Utils.trim(username),
                    password,
                    "form username/password"));
        }
    }

    public void lookupStoredCredentials() {
        URL urlObj;
        try {
            urlObj = new URL(this.url);
        } catch (Exception ex) {
            addCredentials();
            return;
        }

        String host = urlObj.getHost();
        addCredentials(new HostnameRequirement(host));
    }

    public boolean checkOk() {
        return !credentials.isEmpty();
    }

    private void addCredentials(DomainRequirement ...domainRequirement) {
        List<StandardUsernamePasswordCredentials> list = CredentialsProvider.lookupCredentials(
                StandardUsernamePasswordCredentials.class,
                Jenkins.getInstance(),
                ACL.SYSTEM,
                domainRequirement);

        for (StandardUsernamePasswordCredentials storedCredential : list) {
            credentials.add(new EaseUser(
                    storedCredential.getUsername(),
                    storedCredential.getPassword(),
                    CredentialsNameProvider.name(storedCredential)));
        }
    }

    public boolean authenticate(final EASEEndpoint endpoint) {
        for (EaseUser user : credentials) {
            try {
                if (endpoint.tryLogin(user.getUsername(), user.getPassword().getPlainText())) {
                    return true;
                }
            } catch (Exception e) {
                String message = "Could authenticate to '" + endpoint.url +
                        "', credentials used=" + user.getDescription() +
                        ", error='" + e.getMessage() + "'";
                logger.log(Level.WARNING, message, e);
                endpoint.setLastLoginError(message);
            }
        }
        return false;
    }

    public String getLastCredentialDescription() {
        if (credentials.isEmpty()) {
            return "no credentials";
        }
        return credentials.listIterator().previous().getDescription();
    }

    private static class ErrorAuthenticateUserResponse extends AuthenticateUserResponse {
        private final String errMsg;

        public ErrorAuthenticateUserResponse(String errMsg) {
            this.errMsg = errMsg;
        }

        @Override
        public String getErrorMessage() {
            return errMsg;
        }
    }
}
