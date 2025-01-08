package com.pathcreator.hive.repository.impl;

import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.ChunkCheckerException;
import com.pathcreator.hive.exception.ChunkCleanerException;
import com.pathcreator.hive.exception.ChunkLoaderException;
import com.pathcreator.hive.exception.ChunkedInputStreamException;
import com.pathcreator.hive.io.*;
import com.pathcreator.hive.repository.HiveRepository;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Component
public class HiveRepositoryImpl implements HiveRepository {

    @Override
    public String save(BytesStream bytesStream, String directory, POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter, boolean crypt) throws ChunkedInputStreamException {
        try (ChunkedInputStream chunkedInputStream = new ChunkedInputStream(directory)) {
            if (crypt) {
                return chunkedInputStream.processEncryptedChunks(key, nonce, counter, pow, bytesStream.getTable());
            } else {
                return chunkedInputStream.processChunksToDirectory(pow, bytesStream.getTable());
            }
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to save data", e);
        }
    }

    @Override
    public String save(InputStream inputStream, Integer chunkSize, String directory, POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter) throws ChunkedInputStreamException {
        try (inputStream; ChunkedInputStream chunkedInputStream = new ChunkedInputStream(inputStream, chunkSize, directory)) {
            return chunkedInputStream.processEncryptedChunks(key, nonce, counter, pow);
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to save data", e);
        }
    }

    @Override
    public String save(Map<Long, Map<Integer, byte[]>> table, Integer chunkSize, String directory, POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter) throws ChunkedInputStreamException {
        try (ChunkedInputStream chunkedInputStream = new ChunkedInputStream(chunkSize, directory)) {
            return chunkedInputStream.processEncryptedChunks(key, nonce, counter, pow, table);
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to save data", e);
        }
    }

    @Override
    public void retrieve(OutputStream outputStream, String directory, String fileName, byte[] key, byte[] nonce, Integer counter, boolean crypt) throws ChunkLoaderException {
        ChunkLoader chunkLoader = new ChunkLoader(directory, fileName);
        if (crypt) {
            chunkLoader.loadDecryptedChunksAsync(key, nonce, counter, outputStream);
        } else {
            chunkLoader.loadChunksAsynchronously(outputStream);
        }
    }

    @Override
    public byte[] retrieveAsBytes(String directory, String fileName, byte[] key, byte[] nonce, Integer counter) throws ChunkLoaderException {
        ChunkLoader chunkLoader = new ChunkLoader(directory, fileName);
        return chunkLoader.loadDecryptedChunksToBytes(key, nonce, counter);
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