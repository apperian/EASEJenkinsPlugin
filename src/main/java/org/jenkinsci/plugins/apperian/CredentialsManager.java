package org.jenkinsci.plugins.apperian;

import java.util.Collections;
import java.util.List;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import hudson.model.Run;
import hudson.model.Item;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import org.acegisecurity.Authentication;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import hudson.security.ACL;
import jenkins.model.Jenkins;

public class CredentialsManager {

    public static String getCredentialWithId(String credentialId, Item job) {
        List<StringCredentials> stringCredentials = fetchStringCredentials(job);

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

    private static List<StringCredentials> fetchStringCredentials(Item job) {
        Authentication authentication = ACL.SYSTEM;
        if (job instanceof Queue.Task) {
            authentication = Tasks.getAuthenticationOf((Queue.Task)job);
        }

        if (job == null) {
            return CredentialsProvider.lookupCredentials(
                StringCredentials.class,
                Jenkins.getInstance(),
                authentication,
                Collections.<DomainRequirement>emptyList()
            );
        }

        return CredentialsProvider.lookupCredentials(
            StringCredentials.class,
            job,
            authentication,
            Collections.<DomainRequirement>emptyList()
        );
    }
}
