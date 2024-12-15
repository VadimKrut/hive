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
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class HiveSimpleLimitNoCryptRepositoryImpl implements HiveSimpleLimitNoCryptRepository {

    @Override
    public String save(InputStream inputStream) throws ChunkedInputStreamException {
        try (ChunkedInputStream chunkedInputStream = new ChunkedInputStream(inputStream, null, null)) {
            return chunkedInputStream.processChunksToDirectory(POW_UNIQ.MEDIUM_UNIQ);
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to save data", e);
        }
    }

    @Override
    public byte[] retrieveAsBytes(String fileName) throws ChunkLoaderException {
        ChunkLoader chunkLoader = new ChunkLoader(null, fileName);
        return chunkLoader.loadChunksToBytes();
    }

    @Override
    public void delete(String fileName) throws ChunkCleanerException {
        ChunkCleaner cleaner = new ChunkCleaner(null, fileName);
        cleaner.cleanChunks();
    }

    @Override
    public boolean exists(String fileName) throws ChunkCheckerException {
        ChunkChecker checker = new ChunkChecker(null, fileName);
        return checker.exists();
    }
}