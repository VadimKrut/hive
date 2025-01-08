package com.pathcreator.hive.controller;

import com.pathcreator.hive.io.BytesStream;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Path("/hive-simple-limit-no-crypt")
@Tag(name = "hive-simple-limit-no-crypt")
public interface HiveSimpleLimitNoCryptController {

    @Operation(summary = "Save a file", description = "Save a file",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schemaProperties = {
                                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary", description = "File to save"))
                            }
                    )
            ))
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
            @Parameter(description = "Size of each chunk in bytes") @HeaderParam("chunk-size") Integer chunkSize,
            @Parameter(description = "Unique string for identity user") @QueryParam("uniq") String uniq,
            @Parameter(description = "File to save", required = true) @RequestBody BytesStream bytesStream
    );

    @Operation(
            summary = "Load a file",
            description = "Load a file"
    )
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiResponses({
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_OK, description = "the request was successful",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM, schema = @Schema(implementation = StreamingOutput.class))),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_BAD_REQUEST, description = """
                    * id is null
                    """),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_NOT_FOUND, description = "file not found")
    })
    Response getFile(
            @Parameter(description = "The unique identifier of the file in the repository", required = true) @PathParam("id") String id,
            @Parameter(description = "Unique string for identity user") @QueryParam("uniq") String uniq,
            @Parameter(description = "Upon receipt, download immediately or display?, if not transmitted, it will be true, therefore, the file will immediately start downloading upon receipt.") @QueryParam("disposition") Boolean disposition,
            @Parameter(description = "File Type", example = "pdf") @QueryParam("type") String type
    );

    @Operation(
            summary = "Delete a file",
            description = "Delete a file"
    )
    @DELETE
    @Path("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_OK, description = "the request was successful"),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_BAD_REQUEST, description = """
                    * id is null
                    """),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_NOT_FOUND, description = "file not found")
    })
    void deleteFile(
            @Parameter(description = "The unique identifier of the file in the repository", required = true) @PathParam("id") String id,
            @Parameter(description = "Unique string for identity user") @QueryParam("uniq") String uniq
    );
}