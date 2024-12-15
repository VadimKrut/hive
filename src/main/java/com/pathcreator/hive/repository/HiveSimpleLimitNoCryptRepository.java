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
     * @return имя директории, где сохранены данные.
     * @throws ChunkedInputStreamException в случае ошибок при сохранении.
     */
    String save(InputStream inputStream) throws ChunkedInputStreamException;

    /**
     * Получает данные из базы данных в виде массива байтов.
     *
     * @param fileName имя директории с данными.
     * @return массив байтов с данными.
     * @throws ChunkLoaderException в случае ошибок при загрузке.
     */
    byte[] retrieveAsBytes(String fileName) throws ChunkLoaderException;

    /**
     * Удаляет данные из базы данных.
     *
     * @param fileName имя директории с данными.
     * @throws ChunkCleanerException в случае ошибок при удалении.
     */
    void delete(String fileName) throws ChunkCleanerException;

    /**
     * Проверяет, существует ли директория или файл.
     *
     * @param fileName имя файла или директории.
     * @return true, если файл или директория существуют; false в противном случае.
     * @throws ChunkCheckerException в случае ошибок при проверке.
     */
    boolean exists(String fileName) throws ChunkCheckerException;
}