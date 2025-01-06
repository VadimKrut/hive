package com.pathcreator.hive.configuration;

import com.pathcreator.hive.io.BytesStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.pathcreator.hive.util.ChunkUtils.defaultChunkSize;

@Component
public class BytesStreamMessageConverter implements HttpMessageConverter<BytesStream> {

    @Value(value = "${chunk.size}")
    private Integer chunkSize;

    @Override
    public boolean canRead(@NonNull Class<?> clazz, MediaType mediaType) {
        return BytesStream.class.isAssignableFrom(clazz) && MediaType.APPLICATION_OCTET_STREAM.includes(mediaType);
    }

    @Override
    public boolean canWrite(@NonNull Class<?> clazz, MediaType mediaType) {
        return BytesStream.class.isAssignableFrom(clazz) && MediaType.APPLICATION_OCTET_STREAM.includes(mediaType);
    }

    @NonNull
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM);
    }

    @NonNull
    @Override
    public BytesStream read(@NonNull Class<? extends BytesStream> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        if (chunkSize == null || chunkSize <= 0) {
            return new BytesStream(inputMessage.getBody(), defaultChunkSize());
        } else {
            return new BytesStream(inputMessage.getBody(), chunkSize);
        }
    }

    @Override
    public void write(@NonNull BytesStream bytesStream, MediaType contentType, @NonNull HttpOutputMessage outputMessage)
            throws IOException {
        if (contentType != null) {
            outputMessage.getHeaders().setContentType(contentType);
        }
        try (var outputStream = outputMessage.getBody()) {
            bytesStream.completeOutPutStream(outputStream);
        }
    }
}