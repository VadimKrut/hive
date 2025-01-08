package com.pathcreator.hive.repository;

import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.ChunkCheckerException;
import com.pathcreator.hive.exception.ChunkCleanerException;
import com.pathcreator.hive.exception.ChunkLoaderException;
import com.pathcreator.hive.exception.ChunkedInputStreamException;
import com.pathcreator.hive.io.BytesStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface HiveRepository {

    String save(BytesStream bytesStream, String directory, POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter, boolean crypt) throws ChunkedInputStreamException;

    String save(InputStream inputStream, Integer chunkSize, String directory, POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter) throws ChunkedInputStreamException;

    String save(Map<Long, Map<Integer, byte[]>> table, Integer chunkSize, String directory, POW_UNIQ pow, byte[] key, byte[] nonce, Integer counter) throws ChunkedInputStreamException;

    void retrieve(OutputStream outputStream, String directory, String fileName, byte[] key, byte[] nonce, Integer counter, boolean crypt) throws ChunkLoaderException;

    byte[] retrieveAsBytes(String directory, String fileName, byte[] key, byte[] nonce, Integer counter) throws ChunkLoaderException;

    void delete(String directory, String fileName) throws ChunkCleanerException;

    boolean exists(String directory, String fileName) throws ChunkCheckerException;
}