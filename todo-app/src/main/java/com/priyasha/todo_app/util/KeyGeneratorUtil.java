package com.priyasha.todo_app.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class KeyGeneratorUtil {

    public static SecretKey generateKey() {
        // Generate a secure random key for HS256
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public static void main(String[] args) {
        SecretKey secretKey = generateKey();
        System.out.println("Generated Secret Key: " + secretKey);
    }
}
