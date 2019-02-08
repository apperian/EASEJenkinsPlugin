package org.jenkinsci.plugins.apperian;

import java.util.Base64;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import hudson.security.ACL;
import jenkins.model.Jenkins;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CredentialsManager {

    static final Logger logger = Logger.getLogger(CredentialsManager.class.getName());
    private static final String secretKeyString = "UnIqUekEyAbCdEfG";

    public List<ApiToken> getCredentials() {
        final List<ApiToken> apiTokens = new ArrayList<>();
        List<StringCredentials> stringCredentials = fetchStringCredentials();

        for (StringCredentials storedCredential : stringCredentials) {
            apiTokens.add(new ApiToken(
                    encrypt(storedCredential.getSecret().getPlainText()),
                    CredentialsNameProvider.name(storedCredential)));
        }
        return apiTokens;
    }

    // NOTE:  This method can ONLY be run on the master Jenkins instance (because it calls 'Jenkins.getInstance()').
    //        It's used by the ApperianUpload object, but only by functions referenced from the UI.  We encrypt the
    //        API Token and store it locally to avoid calling back to master.
    private List<StringCredentials> fetchStringCredentials() {
        return CredentialsProvider.lookupCredentials(
            StringCredentials.class,
            Jenkins.getInstance(),
            ACL.SYSTEM,
            Collections.<DomainRequirement>emptyList()
        );
    }

    private static SecretKeySpec getSecretKey()
    {
        SecretKeySpec secretKey = null;
        try {
            byte[] key = secretKeyString.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (Exception e) {
            logger.info("Error retrieving secret key: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return secretKey;
    }

    public static String encrypt(String strToEncrypt)
    {
        String encryptedString = null;
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedString = encoder.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            logger.info("Error encrypting: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return encryptedString;
    }


    public static String decrypt(String strToDecrypt)
    {
        String decryptedString = null;
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            Base64.Decoder decoder = Base64.getDecoder();
            decryptedString = new String(cipher.doFinal(decoder.decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            logger.info("Error while decrypting: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return decryptedString;
    }
}
