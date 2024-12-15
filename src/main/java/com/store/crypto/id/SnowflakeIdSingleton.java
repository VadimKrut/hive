package com.store.crypto.id;

public class SnowflakeIdSingleton {
    private static final SnowflakeIdService instance = new SnowflakeIdService(1, 1);

    public static SnowflakeIdService getInstance() {
        return instance;
    }
}