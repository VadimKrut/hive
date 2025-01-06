package com.pathcreator.hive.io;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

public class FragmentInputStream extends InputStream {

    private final InputStream originalStream;
    @Getter
    private final int chunkSize;

    public FragmentInputStream(InputStream originalStream, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than 0");
        }
        this.originalStream = originalStream;
        this.chunkSize = chunkSize;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        if (buffer.length < chunkSize) {
            throw new IllegalArgumentException("Buffer size must be at least " + chunkSize);
        }
        int bytesRead = 0;
        while (bytesRead < chunkSize) {
            int result = originalStream.read(buffer, bytesRead, chunkSize - bytesRead);
            if (result == -1) {
                break;
            }
            bytesRead += result;
        }
        return bytesRead == 0 ? -1 : bytesRead;
    }

    @Override
    public int read() throws IOException {
        return originalStream.read();
    }

    @Override
    public void close() throws IOException {
        originalStream.close();
    }
}