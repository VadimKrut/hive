package com.pathcreator.hive.exception;

public class ChunkLoaderException extends Exception {

    public ChunkLoaderException(String message) {
        super(message);
    }

    public ChunkLoaderException(String message, Exception cause) {
        super(message, cause);
    }
}