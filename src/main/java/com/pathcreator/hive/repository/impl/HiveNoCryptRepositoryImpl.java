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
import com.pathcreator.hive.repository.HiveNoCryptRepository;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
public class HiveNoCryptRepositoryImpl implements HiveNoCryptRepository {

    @Override
    public String save(InputStream inputStream, Integer chunkSize, String directory, POW_UNIQ pow) throws ChunkedInputStreamException {
        try (inputStream; ChunkedInputStream chunkedInputStream = new ChunkedInputStream(inputStream, chunkSize, directory)) {
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