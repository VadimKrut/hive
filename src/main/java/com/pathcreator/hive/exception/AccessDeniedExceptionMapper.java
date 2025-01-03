package com.pathcreator.hive.exception;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

@Slf4j
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException ex) {
        log.error("Access denied", ex);
        return Response
                .status(HttpServletResponse.SC_FORBIDDEN)
                .entity(new ApiException(403, ex.getMessage()).toString())
                .build();
    }
}