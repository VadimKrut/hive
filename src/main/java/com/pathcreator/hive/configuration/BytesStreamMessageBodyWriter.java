package com.pathcreator.hive.configuration;

import com.pathcreator.hive.io.BytesStream;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class BytesStreamMessageBodyWriter implements MessageBodyWriter<BytesStream> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return BytesStream.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(BytesStream bytesStream, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, jakarta.ws.rs.core.MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException {
        bytesStream.completeOutPutStream(entityStream);
    }
}