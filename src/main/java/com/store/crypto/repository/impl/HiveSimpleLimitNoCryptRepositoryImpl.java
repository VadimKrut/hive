package com.store.crypto.repository.impl;

import com.store.crypto.enums.POW_UNIQ;
import com.store.crypto.exception.ChunkCheckerException;
import com.store.crypto.exception.ChunkCleanerException;
import com.store.crypto.exception.ChunkLoaderException;
import com.store.crypto.exception.ChunkedInputStreamException;
import com.store.crypto.repository.HiveSimpleLimitNoCryptRepository;
import com.store.crypto.io.ChunkChecker;
import com.store.crypto.io.ChunkCleaner;
import com.store.crypto.io.ChunkLoader;
import com.store.crypto.io.ChunkedInputStream;

import java.io.InputStream;

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