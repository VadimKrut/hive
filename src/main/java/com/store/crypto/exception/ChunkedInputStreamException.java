package com.store.crypto.exception;

public class ChunkedInputStreamException extends Exception {

    public ChunkedInputStreamException(String message) {
        super(message);
    }

    public ChunkedInputStreamException(String message, Exception e) {
        super(message, e);
    }
}