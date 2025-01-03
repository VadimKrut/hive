package com.pathcreator.hive.exception;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * A custom exception mapper `NotFoundExceptionMapper` that implements the `ExceptionMapper` interface
 * for mapping instances of `NotFoundException` to appropriate HTTP responses.
 */
@Slf4j
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    /**
     * This method is invoked when an instance of `NotFoundException` is thrown,
     * and it converts the exception to a Response object.
     *
     * @param ex The `NotFoundException` instance that was thrown
     * @return A Response object with the appropriate status code and entity
     */
    @Override
    public Response toResponse(NotFoundException ex) {
        log.error("NotFoundException: ", ex);  // Log the error with the exception
        return Response
                .status(HttpServletResponse.SC_NOT_FOUND)  // Set the HTTP status code to 404 (Not Found)
                .entity(new ApiException(404, ex.getMessage()).toString())  // Create a response entity with JSON-like representation of the exception
                .build();  // Build the Response object
    }
}