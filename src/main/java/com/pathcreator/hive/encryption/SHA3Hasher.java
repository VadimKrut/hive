package com.pathcreator.hive.encryption;

import com.pathcreator.hive.exception.SHA3HasherException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

public class SHA3Hasher {

    protected static final int MIN_KEY_LENGTH = 16;
    protected static final int MAX_KEY_LENGTH = 64;
    protected static final int KEY_LENGTH = 256;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String generateUniqueString() throws SHA3HasherException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("DEFAULT", "BC");
            byte[] another = new byte[getRandomLength(secureRandom)];
            byte[] secretKey = new byte[getRandomLength(secureRandom)];
            secureRandom.nextBytes(another);
            secureRandom.nextBytes(secretKey);
            byte[] result = generateString(another, secretKey);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (Exception e) {
            throw new SHA3HasherException("Ошибка при генерации уникальной строки", e);
        }
    }

    protected static byte[] generateString(byte[] another, byte[] secretKey) {
        Mac hmac = new HMac(new SHA3Digest(KEY_LENGTH));
        hmac.init(new KeyParameter(secretKey));
        hmac.update(another, 0, another.length);
        byte[] result = new byte[hmac.getMacSize()];
        hmac.doFinal(result, 0);
        return result;
    }

    protected static int getRandomLength(SecureRandom secureRandom) {
        return secureRandom.nextInt((SHA3Hasher.MAX_KEY_LENGTH - SHA3Hasher.MIN_KEY_LENGTH) + 1) + SHA3Hasher.MIN_KEY_LENGTH;
    }
}