package com.store.crypto.hive.impl;

import com.store.crypto.enums.POW_UNIQ;
import com.store.crypto.exception.ChunkCheckerException;
import com.store.crypto.exception.ChunkCleanerException;
import com.store.crypto.exception.ChunkLoaderException;
import com.store.crypto.exception.ChunkedInputStreamException;
import com.store.crypto.hive.HiveRepository;
import com.store.crypto.io.ChunkChecker;
import com.store.crypto.io.ChunkCleaner;
import com.store.crypto.io.ChunkLoader;
import com.store.crypto.io.ChunkedInputStream;

import java.io.InputStream;
import java.io.OutputStream;

public class HiveRepositoryImpl implements HiveRepository {

    @Override
    public String save(InputStream inputStream, Integer chunkSize, String directory, POW_UNIQ pow) throws ChunkedInputStreamException {
        try (ChunkedInputStream chunkedInputStream = new ChunkedInputStream(inputStream, chunkSize, directory)) {
            return chunkedInputStream.processChunksToDirectory(pow);
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to save data", e);
        }
    }

    @Override
    public void retrieve(OutputStream outputStream, String directory, String fileName) throws ChunkLoaderException {
        ChunkLoader chunkLoader = new ChunkLoader(directory, fileName);
        chunkLoader.loadChunksAsynchronously(outputStream);
    }

    @Override
    public byte[] retrieveAsBytes(String directory, String fileName) throws ChunkLoaderException {
        ChunkLoader chunkLoader = new ChunkLoader(directory, fileName);
        return chunkLoader.loadChunksToBytes();
    }

    @Override
    public void delete(String directory, String fileName) throws ChunkCleanerException {
        ChunkCleaner cleaner = new ChunkCleaner(directory, fileName);
        cleaner.cleanChunks();
    }

    @Override
    public boolean exists(String directory, String fileName) throws ChunkCheckerException {
        ChunkChecker checker = new ChunkChecker(directory, fileName);
        return checker.exists();
    }
}