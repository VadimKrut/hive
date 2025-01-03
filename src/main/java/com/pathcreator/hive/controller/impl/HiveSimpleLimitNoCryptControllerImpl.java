package com.pathcreator.hive.controller.impl;

import com.pathcreator.hive.controller.HiveSimpleLimitNoCryptController;
import com.pathcreator.hive.service.HiveSimpleLimitNoCryptService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class HiveSimpleLimitNoCryptControllerImpl implements HiveSimpleLimitNoCryptController {

    private final HiveSimpleLimitNoCryptService hiveSimpleLimitNoCryptService;

    @Override
    public Response save(String uniq, InputStream inputStream) {
        return hiveSimpleLimitNoCryptService.save(uniq, inputStream);
    }
}