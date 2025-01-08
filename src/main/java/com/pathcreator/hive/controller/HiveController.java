package com.pathcreator.hive.controller;

import com.pathcreator.hive.enums.POW_UNIQ;
import com.pathcreator.hive.exception.ApiException;
import com.pathcreator.hive.io.BytesStream;
import com.pathcreator.hive.util.SecureString;
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
@Path("/hive")
@Tag(name = "hive")
public interface HiveController {

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
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_BAD_REQUEST, description = """
                    * Invalid key: must be 32 bytes.
                    * Invalid nonce: must be 12 bytes.
                    * Invalid counter: counter must be between 0 and 1073741823
                    """, content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_METHOD_NOT_ALLOWED, description = "Something", content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    Response save(
            @Parameter(description = "Size of each chunk in bytes") @HeaderParam("chunk-size") Integer chunkSize,
            @Parameter(description = "Type of id file") @QueryParam("pow") POW_UNIQ pow,
            @Parameter(description = "Directory to save the file") @QueryParam("directory") String directory,
            @Parameter(description = "Key for cryption", schema = @Schema(type = "string")) @QueryParam("key") SecureString key,
            @Parameter(description = "Nonce for cryption", schema = @Schema(type = "string")) @QueryParam("nonce") SecureString nonce,
            @Parameter(description = "Counter for cryption") @QueryParam("counter") Integer counter,
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
                    * Invalid key: must be 32 bytes.
                    * Invalid nonce: must be 12 bytes.
                    * Invalid counter: counter must be between 0 and 1073741823
                    * id is required
                    """, content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_NOT_FOUND, description = "File not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    Response getFile(
            @Parameter(description = "The unique identifier of the file in the repository", required = true) @PathParam("id") String id,
            @Parameter(description = "Directory to save the file") @QueryParam("directory") String directory,
            @Parameter(description = "Key for cryption", schema = @Schema(type = "string")) @QueryParam("key") SecureString key,
            @Parameter(description = "Nonce for cryption", schema = @Schema(type = "string")) @QueryParam("nonce") SecureString nonce,
            @Parameter(description = "Counter for cryption") @QueryParam("counter") Integer counter,
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
                    id is required
                    """, content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_NOT_FOUND, description = "File not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    void deleteFile(
            @Parameter(description = "The unique identifier of the file in the repository", required = true) @PathParam("id") String id,
            @Parameter(description = "Directory to save the file") @QueryParam("directory") String directory
    );
}