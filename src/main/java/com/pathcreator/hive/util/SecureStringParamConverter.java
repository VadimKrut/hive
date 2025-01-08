package com.pathcreator.hive.util;

import jakarta.ws.rs.ext.ParamConverter;

public class SecureStringParamConverter implements ParamConverter<SecureString> {

    @Override
    public SecureString fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new SecureString(value.toCharArray());
    }

    @Override
    public String toString(SecureString secureString) {
        if (secureString == null) {
            return null;
        }
        return secureString.toPlainString();
    }
}