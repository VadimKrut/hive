package com.pathcreator.hive.repository.impl;

import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.ChunkCheckerException;
import com.pathcreator.hive.exception.ChunkCleanerException;
import com.pathcreator.hive.exception.ChunkLoaderException;
import com.pathcreator.hive.exception.ChunkedInputStreamException;
import com.pathcreator.hive.io.ChunkChecker;
import com.pathcreator.hive.io.ChunkCleaner;
import com.pathcreator.hive.io.ChunkLoader;
import com.pathcreator.hive.io.ChunkedInputStream;
import com.pathcreator.hive.repository.HiveSimpleLimitNoCryptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class HiveSimpleLimitNoCryptRepositoryImpl implements HiveSimpleLimitNoCryptRepository {

    @Override
    public String save(InputStream inputStream, String id) throws ChunkedInputStreamException {
        try (inputStream; ChunkedInputStream chunkedInputStream = new ChunkedInputStream(inputStream, null, compileDirectory(id))) {
            return chunkedInputStream.processChunksToDirectory(POW_UNIQ.MEDIUM_UNIQ);
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to save data", e);
        }
    }

    @Override
    public byte[] retrieveAsBytes(String fileName, String id) throws ChunkLoaderException {
        ChunkLoader chunkLoader = new ChunkLoader(compileDirectory(id), fileName);
        return chunkLoader.loadChunksToBytes();
    }

    @Override
    public void delete(String fileName, String id) throws ChunkCleanerException {
        ChunkCleaner cleaner = new ChunkCleaner(compileDirectory(id), fileName);
        cleaner.cleanChunks();
    }

    @Override
    public boolean exists(String fileName, String id) throws ChunkCheckerException {
        ChunkChecker checker = new ChunkChecker(compileDirectory(id), fileName);
        return checker.exists();
    }

    private String compileDirectory(String id) {
        StringBuilder builder = new StringBuilder();
        if (id == null || id.isEmpty()) {
            builder.append("data/");
        } else {
            builder.append("data/").append(id).append("/");
        }
        return builder.toString();
    }
}