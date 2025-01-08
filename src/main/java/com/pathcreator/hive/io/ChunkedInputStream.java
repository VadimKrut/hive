package com.pathcreator.hive.io;

import com.pathcreator.hive.encryption.ChaCha20Cipher;
import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.ChaCha20CipherException;
import com.pathcreator.hive.exception.ChunkedInputStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.pathcreator.hive.util.ChunkUtils.*;

public class ChunkedInputStream extends InputStream {
    protected InputStream sourceStream;
    protected Integer chunkSize;
    protected String directory;
    protected static final int KB_16 = 16384;
    protected static final int KB_64 = 65536;
    protected static final int MB_100 = 104857600;
    protected static final String DEFAULT_DIRECTORY = "data/";

    public ChunkedInputStream(String directory) {
        this.directory = (directory == null || directory.isEmpty()) ? DEFAULT_DIRECTORY : validateDirectory(directory);
    }

    public ChunkedInputStream(Integer chunkSize, String directory) {
        if (chunkSize == null || chunkSize <= KB_16 || chunkSize > MB_100) {
            this.chunkSize = defaultChunkSize();
        } else {
            this.chunkSize = chunkSize;
        }
        this.directory = (directory == null || directory.isEmpty()) ? DEFAULT_DIRECTORY : validateDirectory(directory);
    }

    public ChunkedInputStream(InputStream sourceStream, Integer chunkSize, String directory) throws ChunkedInputStreamException {
        this.sourceStream = Objects.requireNonNull(sourceStream, "Source stream cannot be null");
        if (chunkSize == null || chunkSize <= KB_16 || chunkSize > MB_100) {
            this.chunkSize = KB_64;
        } else {
            this.chunkSize = chunkSize;
        }
        this.directory = (directory == null || directory.isEmpty()) ? DEFAULT_DIRECTORY : validateDirectory(directory);
        validateInputStream();
    }

    public String processChunksToDirectory(POW_UNIQ pow) throws ChunkedInputStreamException {
        return processChunksInternal(pow, null, null, null);
    }

    public String processEncryptedChunks(byte[] key, byte[] nonce, Integer counter, POW_UNIQ pow) throws ChunkedInputStreamException {
        validateCrypto(key, nonce, counter);
        return processChunksInternal(pow, key, nonce, counter);
    }

    public String processChunksToDirectory(POW_UNIQ pow, Map<Long, Map<Integer, byte[]>> table) throws ChunkedInputStreamException {
        if (table == null || table.isEmpty()) {
            throw new ChunkedInputStreamException("Table cannot be null or empty");
        }
        return processChunksInternal(pow, null, null, null, table);
    }

    public String processEncryptedChunks(byte[] key, byte[] nonce, Integer counter, POW_UNIQ pow, Map<Long, Map<Integer, byte[]>> table) throws ChunkedInputStreamException {
        validateCrypto(key, nonce, counter);
        if (table == null || table.isEmpty()) {
            throw new ChunkedInputStreamException("Table cannot be null or empty");
        }
        return processChunksInternal(pow, key, nonce, counter, table);
    }

    private String processChunksInternal(POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter) throws ChunkedInputStreamException {
        byte[] buffer = new byte[chunkSize];
        int bytesRead;
        int chunkIndex = 0;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            String name = uniqString(pow);
            String uniqueDirName = directory + name;
            Files.createDirectories(Paths.get(uniqueDirName));
            while ((bytesRead = sourceStream.read(buffer)) != -1) {
                int currentIndex = chunkIndex++;
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                executor(key, nonce, counter, executor, uniqueDirName, chunk, currentIndex);
            }
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                throw new InterruptedException("Threads interrupted while waiting for chunks to finish");
            }
            return name;
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to process chunks", e);
        }
    }

    private String processChunksInternal(POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter, Map<Long, Map<Integer, byte[]>> table) throws ChunkedInputStreamException {
        int chunkIndex = 0;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            String name = uniqString(pow);
            String uniqueDirName = directory + name;
            Files.createDirectories(Paths.get(uniqueDirName));
            for (long keyOuter : table.keySet()) {
                Map<Integer, byte[]> innerMap = table.get(keyOuter);
                if (innerMap != null) {
                    for (int keyInner : innerMap.keySet()) {
                        byte[] chunk = innerMap.get(keyInner);
                        if (chunk != null) {
                            int currentIndex = chunkIndex++;
                            executor(key, nonce, counter, executor, uniqueDirName, chunk, currentIndex);
                        }
                    }
                }
            }
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                throw new InterruptedException("Threads interrupted while waiting for chunks to finish");
            }
            return name;
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to process chunks", e);
        }
    }

    private void executor(byte[] key, byte[] nonce, Integer counter, ExecutorService executor, String uniqueDirName, byte[] chunk, int currentIndex) {
        executor.submit(() -> {
            try {
                if (counter != null) {
                    writeChunkToFile(uniqueDirName, currentIndex,
                            ChaCha20Cipher.encrypt(key, nonce, counter + currentIndex, chunk));
                } else {
                    writeChunkToFile(uniqueDirName, currentIndex, chunk);
                }
            } catch (IOException | ChaCha20CipherException e) {
                throw new CompletionException("Error saving chunk: " + currentIndex, e);
            }
        });
    }

    protected void validateCrypto(byte[] key, byte[] nonce, Integer counter) throws ChunkedInputStreamException {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(nonce, "Nonce cannot be null");
        Objects.requireNonNull(counter, "Counter cannot be null");
        if (key.length != 32) {
            throw new ChunkedInputStreamException("Key must be 32 bytes");
        }
        if (nonce.length != 12) {
            throw new ChunkedInputStreamException("Nonce must be 12 bytes");
        }
    }

    protected void validateInputStream() throws ChunkedInputStreamException {
        try {
            if (sourceStream.available() == 0) {
                throw new ChunkedInputStreamException("Source stream is empty");
            }
        } catch (IOException e) {
            throw new ChunkedInputStreamException("Error validating source stream", e);
        }
    }

    protected void writeChunkToFile(String dirName, int index, byte[] chunk) throws IOException {
        Files.write(Paths.get(dirName, String.valueOf(index)), chunk);
    }

    @Override
    public int read() throws IOException {
        return sourceStream.read();
    }

    @Override
    public void close() throws IOException {
        if (sourceStream != null) {
            sourceStream.close();
        }
    }
}