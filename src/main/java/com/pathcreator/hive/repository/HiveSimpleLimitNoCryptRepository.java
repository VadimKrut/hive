package com.pathcreator.hive.repository;

import com.pathcreator.hive.exception.ChunkCheckerException;
import com.pathcreator.hive.exception.ChunkCleanerException;
import com.pathcreator.hive.exception.ChunkLoaderException;
import com.pathcreator.hive.exception.ChunkedInputStreamException;

import java.io.InputStream;

public interface HiveSimpleLimitNoCryptRepository {

    /**
     * Сохраняет данные в базу данных.
     *
     * @param inputStream входной поток данных.
     * @param id          уникальный идентификатор
     * @return имя директории, где сохранены данные.
     * @throws ChunkedInputStreamException в случае ошибок при сохранении.
     */
    String save(InputStream inputStream, String id) throws ChunkedInputStreamException;

    /**
     * Получает данные из базы данных в виде массива байтов.
     *
     * @param fileName имя директории с данными.
     * @param id       уникальный идентификатор
     * @return массив байтов с данными.
     * @throws ChunkLoaderException в случае ошибок при загрузке.
     */
    byte[] retrieveAsBytes(String fileName, String id) throws ChunkLoaderException;

    /**
     * Удаляет данные из базы данных.
     *
     * @param fileName имя директории с данными.
     * @param id       уникальный идентификатор
     * @throws ChunkCleanerException в случае ошибок при удалении.
     */
    void delete(String fileName, String id) throws ChunkCleanerException;

    /**
     * Проверяет, существует ли директория или файл.
     *
     * @param fileName имя файла или директории.
     * @param id       уникальный идентификатор
     * @return true, если файл или директория существуют; false в противном случае.
     * @throws ChunkCheckerException в случае ошибок при проверке.
     */
    boolean exists(String fileName, String id) throws ChunkCheckerException;
}