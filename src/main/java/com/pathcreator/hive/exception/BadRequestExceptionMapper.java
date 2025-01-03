package com.pathcreator.hive.exception;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * A custom exception mapper `BadRequestExceptionMapper` that implements the `ExceptionMapper` interface
 * for mapping instances of `BadRequestException` to appropriate HTTP responses.
 */
@Slf4j
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    /**
     * This method is invoked when an instance of `BadRequestException` is thrown,
     * and it converts the exception to a Response object.
     *
     * @param ex The `BadRequestException` instance that was thrown
     * @return A Response object with the appropriate status code and entity
     */
    @Override
    public Response toResponse(BadRequestException ex) {
        log.error("BadRequestException: ", ex);  // Log the error with the exception
        return Response
                .status(HttpServletResponse.SC_BAD_REQUEST)  // Set the HTTP status code to 400 (Bad Request)
                .entity(new ApiException(400, ex.getMessage()).toString())  // Create a response entity with JSON-like representation of the exception
                .build();  // Build the Response object
    }
}