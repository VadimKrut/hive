package com.pathcreator.hive.encryption;

import com.pathcreator.hive.exception.ChaCha20CipherException;

public class ChaCha20Cipher {

    protected static final int[] SIGMA = {
            0x61707865, 0x3320646e, 0x79622d32, 0x6b206574
    };

    protected static final int[][] ROUND_INDICES = {
            {0, 4, 8, 12}, {1, 5, 9, 13}, {2, 6, 10, 14}, {3, 7, 11, 15},
            {0, 5, 10, 15}, {1, 6, 11, 12}, {2, 7, 8, 13}, {3, 4, 9, 14}
    };

    protected static void quarterRound(int[] x, int a, int b, int c, int d) {
        x[a] += x[b];
        x[d] = Integer.rotateLeft(x[d] ^ x[a], 16);
        x[c] += x[d];
        x[b] = Integer.rotateLeft(x[b] ^ x[c], 12);
        x[a] += x[b];
        x[d] = Integer.rotateLeft(x[d] ^ x[a], 8);
        x[c] += x[d];
        x[b] = Integer.rotateLeft(x[b] ^ x[c], 7);
    }

    protected static void chachaBlock(int[] output, int[] input) {
        int[] x = input.clone();
        for (int i = 0; i < 10; i++) {
            for (int[] indices : ROUND_INDICES) {
                quarterRound(x, indices[0], indices[1], indices[2], indices[3]);
            }
        }
        for (int i = 0; i < 16; ++i) {
            output[i] = x[i] + input[i];
        }
    }

    protected static byte[] chacha20(byte[] key, byte[] nonce, int counter, byte[] input) {
        int[] state = new int[16];
        System.arraycopy(SIGMA, 0, state, 0, 4);
        for (int i = 0; i < 8; ++i) {
            state[4 + i] = (key[i * 4] & 0xff) | ((key[i * 4 + 1] & 0xff) << 8)
                    | ((key[i * 4 + 2] & 0xff) << 16) | ((key[i * 4 + 3] & 0xff) << 24);
        }
        state[12] = counter;
        for (int i = 0; i < 3; ++i) {
            state[13 + i] = (nonce[i * 4] & 0xff) | ((nonce[i * 4 + 1] & 0xff) << 8)
                    | ((nonce[i * 4 + 2] & 0xff) << 16) | ((nonce[i * 4 + 3] & 0xff) << 24);
        }
        byte[] output = new byte[input.length];
        int[] temp = new int[16];
        byte[] block = new byte[64];
        int i = 0;
        while (i < input.length) {
            chachaBlock(temp, state);
            for (int j = 0; j < 16; ++j) {
                int word = temp[j];
                block[j * 4] = (byte) word;
                block[j * 4 + 1] = (byte) (word >>> 8);
                block[j * 4 + 2] = (byte) (word >>> 16);
                block[j * 4 + 3] = (byte) (word >>> 24);
            }
            state[12]++;
            int blockSize = Math.min(64, input.length - i);
            for (int j = 0; j < blockSize; ++j) {
                output[i + j] = (byte) (input[i + j] ^ block[j]);
            }
            i += 64;
        }
        return output;
    }

    protected static byte[] validate(byte[] key, byte[] nonce, int counter, byte[] data) throws ChaCha20CipherException {
        try {
            if (key == null || key.length != 32) {
                throw new ChaCha20CipherException("Invalid key");
            }
            if (nonce == null || nonce.length != 12) {
                throw new ChaCha20CipherException("Invalid nonce");
            }
            if (data == null || data.length == 0) {
                throw new ChaCha20CipherException("Invalid data");
            }
            return chacha20(key, nonce, counter, data);
        } catch (Exception e) {
            throw new ChaCha20CipherException("Failed to encrypt or decrypt", e);
        }
    }

    public static byte[] encrypt(byte[] key, byte[] nonce, int counter, byte[] plaintext) throws ChaCha20CipherException {
        return validate(key, nonce, counter, plaintext);
    }

    public static byte[] decrypt(byte[] key, byte[] nonce, int counter, byte[] ciphertext) throws ChaCha20CipherException {
        return validate(key, nonce, counter, ciphertext);
    }
}