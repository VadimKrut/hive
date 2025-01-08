package com.pathcreator.hive.configuration.provider;

import com.pathcreator.hive.util.SecureString;
import com.pathcreator.hive.util.SecureStringParamConverter;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Type;

@Provider
public class SecureStringParamConverterProvider implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, java.lang.annotation.Annotation[] annotations) {
        if (SecureString.class.equals(rawType)) {
            return (ParamConverter<T>) new SecureStringParamConverter();
        }
        return null;
    }
}