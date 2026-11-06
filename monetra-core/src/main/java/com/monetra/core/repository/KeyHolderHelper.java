package com.monetra.core.repository;

import org.springframework.jdbc.support.KeyHolder;

import java.util.Map;

public final class KeyHolderHelper {

    private KeyHolderHelper() {
    }

    public static Long extractId(KeyHolder keyHolder) {
        if (keyHolder == null) return null;

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null) {
            for (Map.Entry<String, Object> entry : keys.entrySet()) {
                if ("id".equalsIgnoreCase(entry.getKey()) && entry.getValue() instanceof Number n) {
                    return n.longValue();
                }
            }
        }

        try {
            Number key = keyHolder.getKey();
            return key != null ? key.longValue() : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
