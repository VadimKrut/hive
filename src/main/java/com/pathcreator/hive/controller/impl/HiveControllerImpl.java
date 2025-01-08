package com.pathcreator.hive.controller.impl;

import com.pathcreator.hive.controller.HiveController;
import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.io.BytesStream;
import com.pathcreator.hive.service.HiveService;
import com.pathcreator.hive.util.SecureString;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HiveControllerImpl implements HiveController {

    private final HiveService hiveService;

    @Override
    public Response save(Integer chunkSize, POW_UNIQ pow, String directory, SecureString key, SecureString nonce, Integer counter, BytesStream bytesStream) {
        return hiveService.save(pow, directory, key, nonce, counter, bytesStream);
    }

    @Override
    public Response getFile(String id, String directory, SecureString key, SecureString nonce, Integer counter, Boolean disposition, String type) {
        return hiveService.getFile(id, directory, key, nonce, counter, disposition, type);
    }

    @Override
    public void deleteFile(String id, String directory) {
        hiveService.deleteFile(id, directory);
    }
}