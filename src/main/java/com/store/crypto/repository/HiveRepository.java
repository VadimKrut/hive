package com.store.crypto.repository;

import com.store.crypto.enums.POW_UNIQ;
import com.store.crypto.exception.ChunkCheckerException;
import com.store.crypto.exception.ChunkCleanerException;
import com.store.crypto.exception.ChunkLoaderException;
import com.store.crypto.exception.ChunkedInputStreamException;

import java.io.InputStream;
import java.io.OutputStream;

public interface HiveRepository {

    /**
     * Сохраняет данные в базу данных.
     *
     * @param inputStream входной поток данных.
     * @param chunkSize   размер чанков.
     * @param directory   директория для сохранения.
     * @param pow         уровень уникальности данных.
     * @return имя директории, где сохранены данные.
     * @throws ChunkedInputStreamException в случае ошибок при сохранении.
     */
    String save(InputStream inputStream, Integer chunkSize, String directory, POW_UNIQ pow) throws ChunkedInputStreamException;

    /**
     * Получает данные из базы данных.
     *
     * @param outputStream поток, куда будут записаны данные.
     * @param directory    директория, где находятся данные.
     * @param fileName     имя директории с данными.
     * @throws ChunkLoaderException в случае ошибок при загрузке.
     */
    void retrieve(OutputStream outputStream, String directory, String fileName) throws ChunkLoaderException;

    /**
     * Получает данные из базы данных в виде массива байтов.
     *
     * @param directory директория, где находятся данные.
     * @param fileName  имя директории с данными.
     * @return массив байтов с данными.
     * @throws ChunkLoaderException в случае ошибок при загрузке.
     */
    byte[] retrieveAsBytes(String directory, String fileName) throws ChunkLoaderException;

    /**
     * Удаляет данные из базы данных.
     *
     * @param directory директория, где находятся данные.
     * @param fileName  имя директории с данными.
     * @throws ChunkCleanerException в случае ошибок при удалении.
     */
    void delete(String directory, String fileName) throws ChunkCleanerException;

    /**
     * Проверяет, существует ли директория или файл.
     *
     * @param directory директория, где находятся данные.
     * @param fileName  имя файла или директории.
     * @return true, если файл или директория существуют; false в противном случае.
     * @throws ChunkCheckerException в случае ошибок при проверке.
     */
    boolean exists(String directory, String fileName) throws ChunkCheckerException;
}