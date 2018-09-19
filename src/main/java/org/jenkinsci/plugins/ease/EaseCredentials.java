package org.jenkinsci.plugins.ease;

import com.apperian.api.JsonHttpEndpoint;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
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

    public List<EaseUser> getCredentials() {
        final List<EaseUser> storedCredentials = new ArrayList<>();
        List<StringCredentials> list = CredentialsProvider.lookupCredentials(
            StringCredentials.class,
                Jenkins.getInstance(),
                ACL.SYSTEM);

        for (StringCredentials storedCredential : list) {
            storedCredentials.add(new EaseUser(
                    storedCredential.getId(),
                    CredentialsNameProvider.name(storedCredential)));
        }
        return storedCredentials;
    }

    public String getCredentialWithId(String credentialId) {
        List<StringCredentials> candidates = CredentialsProvider.lookupCredentials(
            StringCredentials.class,
                Jenkins.getInstance(),
                ACL.SYSTEM);

        StringCredentials credential = CredentialsMatchers.firstOrNull(candidates,                      CredentialsMatchers.withId(credentialId));
        String secret = null;
        if (credential != null) {
            secret = credential.getSecret().getPlainText();
        }
        return secret;
    }

    public boolean checkSessionToken(final JsonHttpEndpoint endpoint) {
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
}
