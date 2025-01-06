package com.pathcreator.hive.controller.impl;

import com.pathcreator.hive.controller.HiveSimpleLimitNoCryptController;
import com.pathcreator.hive.io.BytesStream;
import com.pathcreator.hive.service.HiveSimpleLimitNoCryptService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HiveSimpleLimitNoCryptControllerImpl implements HiveSimpleLimitNoCryptController {

    private final HiveSimpleLimitNoCryptService hiveSimpleLimitNoCryptService;

    @Override
    public Response save(String uniq, BytesStream bytesStream) {
        return hiveSimpleLimitNoCryptService.save(uniq, bytesStream);
    }

    @Override
    public Response getFile(String id, String uniq, Boolean disposition, String type) {
        return hiveSimpleLimitNoCryptService.getFile(id, uniq, disposition, type);
    }

    @Override
    public void deleteFile(String id, String uniq) {
        hiveSimpleLimitNoCryptService.deleteFile(id, uniq);
    }
}