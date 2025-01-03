package com.pathcreator.hive.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom ExceptionMapper to handle any generic Exception.
 * This class maps any unhandled exceptions to a Response with a Internal Server Error status code (500)
 * and logs the error details using SLF4J logger.
 */
@Slf4j
@Provider
public class AnyExceptionMapper implements ExceptionMapper<Exception> {

    /**
     * Maps any generic Exception to a Response with an Internal Server Error status code.
     * Logs the error details using the SLF4J logger.
     *
     * @param e The Exception that was thrown
     * @return Response with an Internal Server Error status code and an error message
     */
    @Override
    public Response toResponse(Exception e) {
        log.error("Error: {}", e.getMessage(), e);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)  // Set the status code to Internal Server Error (500)
                .entity(new ApiException(500, "An error occurred, this functionality is temporarily unavailable").toString())  // Set the response entity as an error message
                .build();  // Build and return the Response
    }
}