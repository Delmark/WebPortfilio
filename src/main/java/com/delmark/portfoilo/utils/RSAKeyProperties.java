package com.delmark.portfoilo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.atmosphere.config.service.Get;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@Component
@Getter
@Setter
public class RSAKeyProperties {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSAKeyProperties() throws NoSuchAlgorithmException {
        KeyPair pair = KeyGeneratorUtility.generateRSAKey();
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }
}
