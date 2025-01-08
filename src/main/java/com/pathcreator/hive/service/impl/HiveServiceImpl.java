package com.pathcreator.hive.service.impl;

import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.ApiException;
import com.pathcreator.hive.io.BytesStream;
import com.pathcreator.hive.repository.HiveRepository;
import com.pathcreator.hive.service.HiveService;
import com.pathcreator.hive.util.SecureString;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.pathcreator.hive.util.ExecutionTime.measureExecutionTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HiveServiceImpl implements HiveService {

    private final HiveRepository hiveRepository;

    @Override
    public Response save(POW_UNIQ pow, String directory, SecureString key, SecureString nonce, Integer counter, BytesStream bytesStream) {
        return measureExecutionTime("HiveRepository: save", () -> {
            boolean crypt = false;
            byte[] keyBytes = new byte[0];
            byte[] nonceBytes = new byte[0];
            if (key != null || nonce != null || counter != null) {
                validateKey(key);
                validateNonce(nonce);
                validateCounter(counter);
                keyBytes = key.toByteArray();
                nonceBytes = nonce.toByteArray();
                crypt = true;
            }
            try (bytesStream) {
                return Response.ok(hiveRepository.save(bytesStream, directory, pow, keyBytes, nonceBytes, counter, crypt)).build();
            } catch (Exception e) {
                throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
            } finally {
                if (crypt) {
                    key.clear();
                    nonce.clear();
                }
            }
        });
    }

    @Override
    public Response getFile(String id, String directory, SecureString key, SecureString nonce, Integer counter, Boolean disposition, String type) {
        if (disposition == null) {
            disposition = Boolean.TRUE;
        }
        if (type == null) {
            type = "bin";
        }
        Boolean finalDisposition = disposition;
        String finalType = type;
        return measureExecutionTime("HiveRepository: getFile", () -> {
            if (id == null || id.isEmpty()) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "id is required");
            }
            boolean crypt = false;
            byte[] keyBytes = new byte[0];
            byte[] nonceBytes = new byte[0];
            if (key != null || nonce != null || counter != null) {
                validateKey(key);
                validateNonce(nonce);
                validateCounter(counter);
                keyBytes = key.toByteArray();
                nonceBytes = nonce.toByteArray();
                crypt = true;
            }
            byte[] finalKeyBytes = keyBytes;
            byte[] finalNonceBytes = nonceBytes;
            boolean finalCrypt = crypt;
            StreamingOutput streamingOutput = outputStream -> {
                try {
                    hiveRepository.retrieve(outputStream, directory, id, finalKeyBytes, finalNonceBytes, counter, finalCrypt);
                } catch (Exception e) {
                    throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "File not found");
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

    @Override
    public void deleteFile(String id, String directory) {
        if (id == null) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "id is required");
        }
        try {
            hiveRepository.delete(directory, id);
        } catch (Exception e) {
            throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }

    private void validateKey(SecureString key) {
        if (key == null || key.length() != 32) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid key: must be 32 bytes.");
        }
    }

    private void validateNonce(SecureString nonce) {
        if (nonce == null || nonce.length() != 12) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid nonce: must be 12 bytes.");
        }
    }

    private void validateCounter(Integer counter) {
        if (counter == null || counter < 0 || counter > (Integer.MAX_VALUE / 2)) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid counter: counter must be between 0 and 1073741823");
        }
    }
}