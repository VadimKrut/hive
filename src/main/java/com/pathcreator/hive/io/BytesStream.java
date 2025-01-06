package com.pathcreator.hive.io;

import com.pathcreator.hive.annotation.IntLimited;
import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.pathcreator.hive.util.ChunkUtils.defaultChunkSize;

@Getter
public class BytesStream implements Closeable, Serializable {

    @Serial
    private static final long serialVersionUID = 531423632698773504L;

    protected Map<Long, Map<Integer, byte[]>> table = new HashMap<>();
    protected transient FragmentInputStream inputStream;

    public BytesStream(InputStream body) {
        Objects.requireNonNull(body, "InputStream cannot be null");
        this.inputStream = new FragmentInputStream(body, defaultChunkSize());
        readFromStream();
    }

    public BytesStream(InputStream body, int chunkSize) {
        Objects.requireNonNull(body, "InputStream cannot be null");
        this.inputStream = new FragmentInputStream(body, chunkSize);
        readFromStream();
    }

    protected void readFromStream() {
        long blockId = 0;
        byte[] buffer = new byte[inputStream.getChunkSize()];
        int bytesRead;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] chunk = java.util.Arrays.copyOf(buffer, bytesRead);
                long currentBlockId = blockId++;
                executor.submit(() -> {
                    HashMap<Integer, byte[]> hashMap = new HashMap<>();
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

    public void setInputStream(InputStream inputStream, int chunkSize) {
        Objects.requireNonNull(inputStream, "InputStream cannot be null");
        this.inputStream = new FragmentInputStream(inputStream, chunkSize);
        table.clear();
        readFromStream();
    }

    @IntLimited
    public byte[] getBytes() throws IOException {
        try (var outputStream = new ByteArrayOutputStream()) {
            completeOutPutStream(outputStream);
            return outputStream.toByteArray();
        }
    }

    public void completeOutPutStream(OutputStream outputStream) throws IOException {
        for (Long key : table.keySet()) {
            byte[] chunk = table.get(key).get(0);
            outputStream.write(chunk);
        }
    }

    @Override
    public void close() throws IOException {
        table.clear();
        if (inputStream != null) {
            inputStream.close();
        }
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}