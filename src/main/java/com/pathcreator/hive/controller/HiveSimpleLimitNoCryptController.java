package com.pathcreator.hive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@Path("/hive-simple-limit-no-crypt")
@Tag(name = "hive-simple-limit-no-crypt")
public interface HiveSimpleLimitNoCryptController {

    @Operation(summary = "Save a file", description = "Save a file")
    @POST
    @Path("/save")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The request was successful",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "InputStream is null"),
            @ApiResponse(responseCode = "405", description = "Some error occurred")
    })
    Response save(
            @Parameter(description = "Unique string for identity user") @QueryParam("uniq") String uniq,
            @Parameter(description = "File to save", required = true) @RequestBody InputStream inputStream
    );
}