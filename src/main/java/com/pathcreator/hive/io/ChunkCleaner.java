package com.pathcreator.hive.io;

import com.pathcreator.hive.exception.ChunkCleanerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ChunkCleaner {

    private final String directory;
    private final String fileName;

    public ChunkCleaner(String directory, String fileName) {
        this.directory = (directory == null || directory.isEmpty()) ? ChunkedInputStream.DEFAULT_DIRECTORY : directory;
        this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
    }

    public void cleanChunks() throws ChunkCleanerException {
        Path dirPath = Path.of(directory).resolve(fileName);
        try {
            File directoryFile = dirPath.toFile();
            if (!directoryFile.exists() || !directoryFile.isDirectory()) {
                throw new ChunkCleanerException("Directory not found: " + dirPath);
            }
            File[] files = directoryFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    Files.deleteIfExists(file.toPath());
                }
                if (Objects.requireNonNull(directoryFile.list()).length == 0) {
                    Files.deleteIfExists(dirPath);
                }
            }
        } catch (IOException e) {
            throw new ChunkCleanerException("Failed to clean chunks in directory: " + dirPath, e);
        }
    }
}