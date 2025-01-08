package com.pathcreator.hive.util;

import java.nio.charset.StandardCharsets;

public final class SecureString implements CharSequence, AutoCloseable {

    private char[] value;

    public SecureString(char[] value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value.clone();
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public char charAt(int index) {
        checkIfClosed();
        return value[index];
    }

    public byte[] toByteArray() {
        checkIfClosed();
        return new String(value).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        checkIfClosed();
        return new SecureString(java.util.Arrays.copyOfRange(value, start, end));
    }

    public char[] toCharArray() {
        checkIfClosed();
        return value.clone();
    }

    public void clear() {
        if (value != null) {
            java.util.Arrays.fill(value, '\0');
            value = null;
        }
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public String toString() {
        return "[SECURE]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SecureString other)) return false;
        checkIfClosed();
        return java.util.Arrays.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        checkIfClosed();
        return java.util.Arrays.hashCode(value);
    }

    private void checkIfClosed() {
        if (value == null) {
            throw new IllegalStateException("SecureString is already cleared or closed.");
        }
    }

    public String toPlainString() {
        checkIfClosed();
        return new String(value);
    }
}