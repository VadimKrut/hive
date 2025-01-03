package com.pathcreator.hive.security;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenUtil {

    public static final int ACCESS_TOKEN_VALIDITY = 120;

    @Value("${jwt.secret}")
    public String secret;

    public Long getUserIdFromToken(String token) {
        try {
            return decryptToken(token).getLongClaim("uid");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user ID from token", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getPermsFromToken(String token) {
        try {
            return (Collection<String>) decryptToken(token).getClaim("perms");
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String generateToken(long userId, String login, String issuer, Set<String> perms) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(login)
                    .issuer(issuer)
                    .claim("uid", userId)
                    .claim("perms", perms != null ? perms : Collections.emptySet())
                    .expirationTime(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY * 60 * 1000))
                    .build();
            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);
            EncryptedJWT jwt = new EncryptedJWT(header, claimsSet);
            DirectEncrypter encrypter = new DirectEncrypter(getSecretKey());
            jwt.encrypt(encrypter);
            return jwt.serialize();
        } catch (Exception e) {
            log.error("Failed to generate token", e);
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    private JWTClaimsSet decryptToken(String token) throws Exception {
        try {
            SecretKey secretKey = getSecretKey();
            EncryptedJWT jwt = EncryptedJWT.parse(token);
            DirectDecrypter decrypter = new DirectDecrypter(secretKey);
            jwt.decrypt(decrypter);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            return claimsSet;
        } catch (Exception e) {
            log.error("Failed to decrypt token", e);
            throw e;
        }
    }

    private SecretKey getSecretKey() {
        byte[] decodedKey = secret.getBytes(StandardCharsets.UTF_8);
        if (decodedKey.length != 32) {
            throw new RuntimeException("Secret key must be 32 bytes for A256GCM encryption.");
        }
        return new SecretKeySpec(decodedKey, "AES");
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = decryptToken(token).getExpirationTime();
            return expirationDate != null && expirationDate.before(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return true;
        }
    }
}