package com.pathcreator.hive.id;

public class SnowflakeIdSingleton {
    private static final SnowflakeIdService instance = new SnowflakeIdService(1, 1);

    public static SnowflakeIdService getInstance() {
        return instance;
    }
}