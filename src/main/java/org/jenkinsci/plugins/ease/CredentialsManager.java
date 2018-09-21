package org.jenkinsci.plugins.ease;

import com.apperian.api.JsonHttpEndpoint;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsManager {
    static final Logger logger = Logger.getLogger(CredentialsManager.class.getName());

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

        StringCredentials credential = CredentialsMatchers.firstOrNull(candidates,
                                                                       CredentialsMatchers.withId(credentialId));
        String secret = null;
        if (credential != null) {
            secret = credential.getSecret().getPlainText();
        }
        return secret;
    }
}
