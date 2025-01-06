package com.pathcreator.hive.service.impl;

import com.pathcreator.hive.exception.ApiException;
import com.pathcreator.hive.io.BytesStream;
import com.pathcreator.hive.repository.HiveSimpleLimitNoCryptRepository;
import com.pathcreator.hive.service.HiveSimpleLimitNoCryptService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.Callable;

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

    @Override
    public Response save(String uniq, BytesStream bytesStream) {
        return measureExecutionTime("HiveSimpleLimitNoCryptController: save: bytesStream", () -> {
            if (bytesStream.getTable() == null || bytesStream.getTable().isEmpty()) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "InputStream is null");
            }
            try (bytesStream) {
                System.out.println(bytesStream.getTable().size());
                return Response.ok(hiveSimpleLimitNoCryptRepository.save(bytesStream.getTable(), uniq)).build();
            } catch (Exception e) {
                throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
            }
        });
    }

    @Override
    public Response getFile(String id, String uniq, Boolean disposition, String type) {
        if (disposition == null) {
            disposition = Boolean.TRUE;
        }
        if (type == null) {
            type = "bin";
        }
        Boolean finalDisposition = disposition;
        String finalType = type;
        return measureExecutionTime("HiveSimpleLimitNoCryptController: getFile", () -> {
            if (id == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "id is null");
            }
            StreamingOutput streamingOutput = outputStream -> {
                try {
                    hiveSimpleLimitNoCryptRepository.retrieve(id, uniq, outputStream);
                } catch (Exception e) {
                    throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while streaming file", e);
                }
            };
            String mimeType = Files.probeContentType(Path.of("exampl." + finalType));
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            Response.ResponseBuilder responseBuilder = Response.ok(streamingOutput);
            responseBuilder.header("Content-Type", mimeType);
            if (Boolean.TRUE.equals(finalDisposition)) {
                String fileName = "file_" + ZonedDateTime.now(ZoneOffset.UTC) + "." + finalType;
                responseBuilder.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            }
            return responseBuilder.build();
        });
    }

    private <T> T measureExecutionTime(String methodName, Callable<T> callable) {
        long startTime = System.currentTimeMillis();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Execution time for {}: {} ms", methodName, duration);
        }
    }
}