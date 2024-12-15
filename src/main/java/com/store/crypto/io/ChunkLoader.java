package com.store.crypto.io;

import com.store.crypto.encryption.ChaCha20Cipher;
import com.store.crypto.exception.ChaCha20CipherException;
import com.store.crypto.exception.ChunkLoaderException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.*;

import static com.store.crypto.io.ChunkedInputStream.DEFAULT_DIRECTORY;
import static com.store.crypto.util.ChunkUtils.validateDirectory;

public class ChunkLoader {

    protected final String directory;
    protected final String fileName;

    public ChunkLoader(String directory, String fileName) {
        this.directory = (directory == null || directory.isEmpty()) ? DEFAULT_DIRECTORY : validateDirectory(directory);
        this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
    }

    public byte[] loadChunksToBytes() throws ChunkLoaderException {
        return loadChunksToBytesInternal(null, null, null);
    }

    public byte[] loadDecryptedChunksToBytes(byte[] key, byte[] nonce, int counter) throws ChunkLoaderException {
        return loadChunksToBytesInternal(key, nonce, counter);
    }

    public void loadChunksAsynchronously(OutputStream outputStream) throws ChunkLoaderException {
        loadChunksInternal(null, null, null, outputStream);
    }

    public void loadDecryptedChunksAsync(byte[] key, byte[] nonce, int counter, OutputStream outputStream) throws ChunkLoaderException {
        loadChunksInternal(key, nonce, counter, outputStream);
    }

    protected void loadChunksInternal(byte[] key, byte[] nonce, Integer counter, OutputStream outputStream) throws ChunkLoaderException {
        ConcurrentHashMap<Integer, CompletableFuture<byte[]>> chunkMap = loadChunksMapAsync(key, nonce, counter);
        writeChunksToStream(outputStream, chunkMap);
    }

    protected byte[] loadChunksToBytesInternal(byte[] key, byte[] nonce, Integer counter) throws ChunkLoaderException {
        ConcurrentHashMap<Integer, CompletableFuture<byte[]>> chunkMap = loadChunksMapAsync(key, nonce, counter);
        return mergeChunksToBytes(chunkMap);
    }

    protected byte[] mergeChunksToBytes(ConcurrentHashMap<Integer, CompletableFuture<byte[]>> chunkMap) throws ChunkLoaderException {
        if (chunkMap.isEmpty()) {
            throw new ChunkLoaderException("Chunk map is empty");
        }
        try {
            int totalSize = chunkMap.values().stream().mapToInt(chunkFuture -> chunkFuture.join().length).sum();
            byte[] result = new byte[totalSize];
            int offset = 0;
            for (int index = 0; index < chunkMap.size(); index++) {
                CompletableFuture<byte[]> future = chunkMap.get(index);
                if (future != null) {
                    byte[] chunk = future.join();
                    System.arraycopy(chunk, 0, result, offset, chunk.length);
                    offset += chunk.length;
                } else {
                    throw new ChunkLoaderException("Missing chunk at index: " + index);
                }
            }
            return result;
        } catch (Exception e) {
            throw new ChunkLoaderException("Failed to merge chunks into byte array", e);
        }
    }

    protected void writeChunksToStream(OutputStream outputStream, ConcurrentHashMap<Integer, CompletableFuture<byte[]>> chunkMap) throws ChunkLoaderException {
        if (chunkMap.isEmpty()) {
            throw new ChunkLoaderException("Chunk map is empty");
        }
        try {
            for (int index = 0; index < chunkMap.size(); index++) {
                CompletableFuture<byte[]> future = chunkMap.get(index);
                if (future != null) {
                    outputStream.write(future.join());
                } else {
                    throw new ChunkLoaderException("Missing chunk at index: " + index);
                }
            }
            outputStream.flush();
        } catch (Exception e) {
            throw new ChunkLoaderException("Failed to process chunks for output stream", e);
        }
    }

    protected ConcurrentHashMap<Integer, CompletableFuture<byte[]>> loadChunksMapAsync(byte[] key, byte[] nonce, Integer counter) throws ChunkLoaderException {
        ConcurrentHashMap<Integer, CompletableFuture<byte[]>> loadedChunks = new ConcurrentHashMap<>();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            File[] files = getSortedFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                int currentCounter = (counter != null) ? counter + i : -1;
                loadedChunks.put(i, CompletableFuture.supplyAsync(() -> readAndDecryptFile(file, key, nonce, currentCounter), executor));
            }
            return loadedChunks;
        } catch (Exception e) {
            throw new ChunkLoaderException("Failed to load chunks asynchronously", e);
        }
    }

    protected File[] getSortedFiles() throws ChunkLoaderException {
        File dir = new File(directory + fileName);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ChunkLoaderException("Invalid directory: " + dir.getAbsolutePath());
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new ChunkLoaderException("No files found in directory: " + dir.getAbsolutePath());
        }
        Arrays.sort(files, Comparator.comparingInt(f -> Integer.parseInt(f.getName())));
        return files;
    }

    protected byte[] readAndDecryptFile(File file, byte[] key, byte[] nonce, int counter) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            if (key != null && nonce != null) {
                return ChaCha20Cipher.decrypt(key, nonce, counter, fileData);
            }
            return fileData;
        } catch (IOException | ChaCha20CipherException e) {
            throw new CompletionException("Failed to read or decrypt file: " + file.getAbsolutePath(), e);
        }
    }
}