package com.pathcreator.hive.configuration;

import com.pathcreator.hive.io.BytesStream;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.pathcreator.hive.util.ChunkUtils.defaultChunkSize;

@Provider
@Component
@Consumes(MediaType.APPLICATION_OCTET_STREAM)
public class BytesStreamMessageBodyReader implements MessageBodyReader<BytesStream> {

    @Value(value = "${chunk.size}")
    private Integer chunkSize;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return BytesStream.class.isAssignableFrom(type);
    }

    @Override
    public BytesStream readFrom(Class<BytesStream> type, Type genericType, Annotation[] annotations,
                                MediaType mediaType, jakarta.ws.rs.core.MultivaluedMap<String, String> httpHeaders,
                                InputStream entityStream) {
        if (chunkSize == null || chunkSize <= 0) {
            return new BytesStream(entityStream, defaultChunkSize());
        } else {
            return new BytesStream(entityStream, chunkSize);
        }
    }
}