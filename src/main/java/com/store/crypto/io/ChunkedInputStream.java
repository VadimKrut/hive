package com.store.crypto.io;

import com.store.crypto.encryption.ChaCha20Cipher;
import com.store.crypto.enums.POW_UNIQ;
import com.store.crypto.exception.ChunkedInputStreamException;
import com.store.crypto.exception.ChaCha20CipherException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.store.crypto.util.ChunkUtils.uniqString;
import static com.store.crypto.util.ChunkUtils.validateDirectory;

public class ChunkedInputStream extends InputStream {
    protected final InputStream sourceStream;
    protected final Integer chunkSize;
    protected String directory;
    protected static final int KB_16 = 16384;
    protected static final int KB_64 = 65536;
    protected static final int MB_100 = 104857600;
    protected static final String DEFAULT_DIRECTORY = "data/";

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
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                throw new InterruptedException("Threads interrupted while waiting for chunks to finish");
            }
            return name;
        } catch (Exception e) {
            throw new ChunkedInputStreamException("Failed to process chunks", e);
        }
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
        sourceStream.close();
    }
}