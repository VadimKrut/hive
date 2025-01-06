package com.pathcreator.hive.service;

import com.pathcreator.hive.io.BytesStream;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface HiveSimpleLimitNoCryptService {

    Response save(String uniq, InputStream inputStream);

    Response save(String uniq, BytesStream bytesStream);

    Response getFile(String id, String uniq, Boolean disposition, String type);

    void deleteFile(String id, String uniq);
}