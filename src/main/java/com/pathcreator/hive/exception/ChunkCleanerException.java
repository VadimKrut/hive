package com.pathcreator.hive.exception;

public class ChunkCleanerException extends Exception {

    public ChunkCleanerException(String message) {
        super(message);
    }

    public ChunkCleanerException(String message, Exception cause) {
        super(message, cause);
    }
}