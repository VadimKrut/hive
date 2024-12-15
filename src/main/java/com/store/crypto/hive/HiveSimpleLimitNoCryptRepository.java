package com.store.crypto.hive;

import com.store.crypto.exception.ChunkCheckerException;
import com.store.crypto.exception.ChunkCleanerException;
import com.store.crypto.exception.ChunkLoaderException;
import com.store.crypto.exception.ChunkedInputStreamException;

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