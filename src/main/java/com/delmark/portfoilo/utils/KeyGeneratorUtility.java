package com.delmark.portfoilo.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyGeneratorUtility {
    public static KeyPair generateRSAKey() throws NoSuchAlgorithmException {

        KeyPair keyPair;

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();

        return keyPair;
    }
}
