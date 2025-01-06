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
                    * hiveId is null
                    """),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_NOT_FOUND, description = "file not found")
    })
    Response getFile(
            @Parameter(description = "Уникальный идентификатор файла в хранилище", required = true) @PathParam("id") String id,
            @Parameter(description = "Unique string for identity user") @QueryParam("uniq") String uniq,
            @Parameter(description = "При получении сразу скачать или отобразить?, если не передать то будет true следовательно файл при получении сразу начнет скачиваться") @QueryParam("disposition") Boolean disposition,
            @Parameter(description = "Тип файла", example = "pdf") @QueryParam("type") String type
    );
}