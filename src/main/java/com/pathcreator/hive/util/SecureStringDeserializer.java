package com.pathcreator.hive.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class SecureStringDeserializer extends JsonDeserializer<SecureString> {

    @Override
    public SecureString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        return new SecureString(value.toCharArray());
    }
}