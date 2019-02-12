package org.jenkinsci.plugins.apperian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import hudson.security.ACL;
import jenkins.model.Jenkins;

public class CredentialsManager {

    static final Logger logger = Logger.getLogger(CredentialsManager.class.getName());

    public List<ApiToken> getCredentials() {
        final List<ApiToken> apiTokens = new ArrayList<>();
        List<StringCredentials> stringCredentials = fetchStringCredentials();

        for (StringCredentials storedCredential : stringCredentials) {
            apiTokens.add(new ApiToken(
                    storedCredential.getId(),
                    CredentialsNameProvider.name(storedCredential)));
        }
        return apiTokens;
    }

    public static String getCredentialWithId(String credentialId) {
        List<StringCredentials> stringCredentials = fetchStringCredentials();

        StringCredentials credential = CredentialsMatchers.firstOrNull(stringCredentials,
                                                                       CredentialsMatchers.withId(credentialId));
        String secret = null;
        if (credential != null) {
            secret = credential.getSecret().getPlainText();
        }
        return secret;
    }

    private static List<StringCredentials> fetchStringCredentials() {
        return CredentialsProvider.lookupCredentials(
            StringCredentials.class,
            Jenkins.getInstance(),
            ACL.SYSTEM,
            Collections.<DomainRequirement>emptyList()
        );
    }
}
