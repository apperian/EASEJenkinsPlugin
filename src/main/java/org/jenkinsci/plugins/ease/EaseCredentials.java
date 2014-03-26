package org.jenkinsci.plugins.ease;

import com.apperian.eas.AuthenticateUserResponse;
import com.apperian.eas.PublishingAPI;
import com.apperian.eas.PublishingEndpoint;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.HostnameRequirement;
import hudson.security.ACL;
import hudson.util.Secret;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EaseCredentials {
    private final String url;
    private final List<EaseUser> credentials;

    public EaseCredentials(String url, String username, String password) {
        this.url = Utils.trim(url);
        credentials = new ArrayList<EaseUser>();
        if (!Utils.isEmptyString(username)) {
            credentials.add(new EaseUser(
                    Utils.trim(username),
                    Secret.fromString(password),
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
        try {
            addCredentials(new HostnameRequirement(host));
        } catch (Exception ex) {
            // skip
        }
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

    public AuthenticateUserResponse authenticate(PublishingEndpoint endpoint)
            throws IOException {
        AuthenticateUserResponse authResponse = null;
        IOException ex = null;
        for (EaseUser user : credentials) {
            try {
                ex = null;
                authResponse = PublishingAPI.authenticateUser(
                        user.getUsername(),
                        user.getPassword().getPlainText())
                        .call(endpoint);
            } catch (IOException e) {
                ex = e;
                continue;
            }
            if (!authResponse.hasError()) {
                break;
            }
            authResponse.appendError(", lastCredentials=" + user.getDescription());
        }
        if (ex != null) {
            throw ex;
        }
        return authResponse;
    }
}
