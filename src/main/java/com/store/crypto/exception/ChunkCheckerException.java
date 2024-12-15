package com.store.crypto.exception;

public class ChunkCheckerException extends Exception {

    public ChunkCheckerException(String message) {
        super(message);
    }

    public ChunkCheckerException(String message, Throwable cause) {
        super(message, cause);
    }
}