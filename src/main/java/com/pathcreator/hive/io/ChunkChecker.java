package com.pathcreator.hive.io;

import com.pathcreator.hive.exception.ChunkCheckerException;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class ChunkChecker {

    private final String directory;
    private final String fileName;

    public ChunkChecker(String directory, String fileName) {
        this.directory = (directory == null || directory.isEmpty()) ? ChunkedInputStream.DEFAULT_DIRECTORY : directory;
        this.fileName = Objects.requireNonNull(fileName, "fileName cannot be null");
    }

    public boolean exists() throws ChunkCheckerException {
        try {
            Path dirPath = Path.of(directory).resolve(fileName);
            File directoryFile = dirPath.toFile();
            return directoryFile.exists();
        } catch (Exception e) {
            throw new ChunkCheckerException("Failed to check existence of directory or file: " + directory + fileName, e);
        }
    }
}