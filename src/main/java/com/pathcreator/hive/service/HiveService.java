package com.pathcreator.hive.service;

import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.io.BytesStream;
import com.pathcreator.hive.util.SecureString;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;

@Service
public interface HiveService {

    Response save(POW_UNIQ pow, String directory, SecureString key, SecureString nonce, Integer counter, BytesStream bytesStream);

    Response getFile(String id, String directory, SecureString key, SecureString nonce, Integer counter, Boolean disposition, String type);

    void deleteFile(String id, String directory);
}