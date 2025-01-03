package com.pathcreator.hive.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * A custom exception mapper `ApiExceptionMapper` that implements the `ExceptionMapper` interface
 * for mapping instances of `ApiException` to appropriate HTTP responses.
 */
@Slf4j
@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    /**
     * This method is invoked when an instance of `ApiException` is thrown,
     * and it converts the exception to a Response object.
     *
     * @param ex The `ApiException` instance that was thrown
     * @return A Response object with the appropriate status code and entity
     */
    @Override
    public Response toResponse(ApiException ex) {
        log.error("Response code: " + ex.getErrorCode(), ex);  // Log the error along with the error code
        return Response
                .status(ex.getErrorCode())  // Set the HTTP status code based on the error code in the exception
                .entity(new ApiException(ex.getErrorCode(), ex.getMessage()).toString())  // Create a response entity with JSON-like representation of the exception
                .build();  // Build the Response object
    }
}
