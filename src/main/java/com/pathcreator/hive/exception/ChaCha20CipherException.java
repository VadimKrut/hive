package com.pathcreator.hive.exception;

public class ChaCha20CipherException extends Exception {

    public ChaCha20CipherException(String message) {
        super(message);
    }

    public ChaCha20CipherException(String message, Exception cause) {
        super(message, cause);
    }
}