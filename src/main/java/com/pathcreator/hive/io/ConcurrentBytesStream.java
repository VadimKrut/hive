package com.pathcreator.hive.io;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentBytesStream extends BytesStream {

    public ConcurrentBytesStream(InputStream body) {
        super(body);
        this.table = new ConcurrentHashMap<>();
    }

    public ConcurrentBytesStream(InputStream body, int chunkSize) {
        super(body, chunkSize);
        this.table = new ConcurrentHashMap<>();
    }

    @Override
    protected void readFromStream() {
        long blockId = 0;
        byte[] buffer = new byte[inputStream.getChunkSize()];
        int bytesRead;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] chunk = java.util.Arrays.copyOf(buffer, bytesRead);
                long currentBlockId = blockId++;
                executor.submit(() -> {
                    ConcurrentHashMap<Integer, byte[]> hashMap = new ConcurrentHashMap<>();
                    hashMap.put(0, chunk);
                    table.put(currentBlockId, hashMap);
                });
            }
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                throw new InterruptedException("Threads interrupted while waiting for chunks to finish");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}