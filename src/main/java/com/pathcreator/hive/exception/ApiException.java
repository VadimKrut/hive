package com.pathcreator.hive.exception;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 4829382930029382039L;

    private int errorCode;

    public ApiException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "{\"errorCode\": " + errorCode + ", \"message\": \"" + getMessage() + "\" }";
    }
}