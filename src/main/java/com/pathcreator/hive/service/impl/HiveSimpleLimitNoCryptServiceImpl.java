package com.pathcreator.hive.service.impl;

import com.pathcreator.hive.exception.ApiException;
import com.pathcreator.hive.repository.HiveSimpleLimitNoCryptRepository;
import com.pathcreator.hive.service.HiveSimpleLimitNoCryptService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class HiveSimpleLimitNoCryptServiceImpl implements HiveSimpleLimitNoCryptService {

    private final HiveSimpleLimitNoCryptRepository hiveSimpleLimitNoCryptRepository;

    @Override
    public Response save(String uniq, InputStream inputStream) {
        if (inputStream == null) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "InputStream is null");
        }
        try {
            return Response.ok(hiveSimpleLimitNoCryptRepository.save(inputStream, uniq)).build();
        } catch (Exception e) {
            throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
        }
    }
}