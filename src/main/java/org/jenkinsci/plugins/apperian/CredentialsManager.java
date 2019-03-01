package org.jenkinsci.plugins.apperian;

import java.util.Collections;
import java.util.List;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import hudson.model.Run;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import hudson.security.ACL;
import jenkins.model.Jenkins;

public class CredentialsManager {

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

    public static String getCredentialWithIdFromRun(Run build, String credentialId) {

        StringCredentials credential = CredentialsProvider.findCredentialById(credentialId,
                StringCredentials.class,
                build,
                Collections.<DomainRequirement>emptyList());
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
