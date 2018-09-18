package org.jenkinsci.plugins.ease;

import com.apperian.api.JsonHttpEndpoint;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.HostnameRequirement;
import hudson.security.ACL;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.api.ApperianEaseEndpoint;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EaseCredentials {
    static final Logger logger = Logger.getLogger(EaseCredentials.class.getName());

    final List<EaseUser> credentials;

    public EaseCredentials(String apiToken) {
        credentials = new ArrayList<>();
        if (!Utils.isEmptyString(apiToken)) {
            credentials.add(
                    new EaseUser(
                            Utils.trim(apiToken),
                            "form api token"));
        }
    }

    public void lookupStoredCredentials(ApperianEaseEndpoint endpoint) {
        List<DomainRequirement> requirements = new ArrayList<>();
        putUrlHostnameAsADomainRequirement(endpoint.getApperianEndpoint().url, requirements);
        putUrlHostnameAsADomainRequirement(endpoint.getEaseEndpoint().url, requirements);
        addCredentials(requirements);
    }

    private void putUrlHostnameAsADomainRequirement(String url,
                                                    List<DomainRequirement> requirements) {
        URL urlObj;
        try {
            urlObj = new URL(url);
            String host = urlObj.getHost();
            requirements.add(new HostnameRequirement(host));
            // TODO add user !
        } catch (Exception ex) {
        }
    }

    public boolean checkOk() {
        return !credentials.isEmpty();
    }

    private void addCredentials(List<DomainRequirement> domainRequirement) {
        List<StringCredentials> list = CredentialsProvider.lookupCredentials(
            StringCredentials.class,
                Jenkins.getInstance(),
                ACL.SYSTEM,
                domainRequirement);

        for (StringCredentials storedCredential : list) {
            credentials.add(new EaseUser(
                    storedCredential.getSecret().getPlainText(),
                    CredentialsNameProvider.name(storedCredential)));
        }
    }

    public boolean checkSessionToken(final JsonHttpEndpoint endpoint) {
        for (EaseUser user : credentials) {
            try {
                endpoint.checkSessionToken(user.getApiToken());
                return true;
            } catch (Exception e) {
                String message = "Could not authenticate to '" + endpoint.getUrl() +
                        "', credentials used=" + user.getDescription() +
                        ", error='" + e.getMessage();
                logger.log(Level.WARNING, message, e);
                endpoint.setLastLoginError(e.getMessage());
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

}
