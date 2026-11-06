package com.monetra.api.security;

public final class UserContext {
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    public static Long getRequiredUserId() {
        Long id = CURRENT_USER_ID.get();
        if (id == null) {
            throw new com.monetra.core.exception.AuthenticationException("User is not authenticated");
        }
        return id;
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}
