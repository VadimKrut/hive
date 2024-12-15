package com.pathcreator.hive.util;

import com.pathcreator.hive.encryption.SHA3Hasher;
import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.SHA3HasherException;
import com.pathcreator.hive.id.SnowflakeIdSingleton;

import java.util.UUID;

public class ChunkUtils {

    public static String validateDirectory(String directory) {
        if (!directory.endsWith("/")) {
            return directory + "/";
        }
        return directory;
    }

    public static int defaultChunkSize() {
        return (int) (Runtime.getRuntime().maxMemory() / Math.pow(Runtime.getRuntime().availableProcessors(), 2));
    }

    public static String uniqString(POW_UNIQ uniq) throws SHA3HasherException {
        if (uniq == POW_UNIQ.LIGHT_UNIQ) {
            return UUID.randomUUID().toString();
        }
        if (uniq == POW_UNIQ.MEDIUM_UNIQ) {
            return String.valueOf(SnowflakeIdSingleton.getInstance().nextId());
        }
        if (uniq == POW_UNIQ.HARD_UNIQ) {
            return SHA3Hasher.generateUniqueString();
        }
        return UUID.randomUUID().toString();
    }
}