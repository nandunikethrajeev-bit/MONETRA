package com.monetra.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long validityInSeconds;

    public JwtTokenProvider(@Value("${monetra.jwt.secret:monetra_secure_finance_system_key_2026_super_secret_token}") String secretKey,
                            @Value("${monetra.jwt.validity-seconds:86400}") long validityInSeconds) {
        this.secretKey = secretKey;
        this.validityInSeconds = validityInSeconds;
    }

    public String generateToken(Long userId, String username) {
        long now = Instant.now().getEpochSecond();
        long exp = now + validityInSeconds;

        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

        String payloadJson = String.format("{\"sub\":\"%d\",\"username\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userId, username, now, exp);
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        String signature = hmacSha256(header + "." + payload, secretKey);
        return header + "." + payload + "." + signature;
    }

    public boolean validateToken(String token) {
        if (token == null || !token.contains(".")) return false;
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String expectedSignature = hmacSha256(parts[0] + "." + parts[1], secretKey);
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            long exp = extractExp(payloadJson);
            return Instant.now().getEpochSecond() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return extractUserId(payloadJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token payload", e);
        }
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error computing HMAC-SHA256", e);
        }
    }

    private long extractExp(String json) {
        int idx = json.indexOf("\"exp\":");
        if (idx == -1) return 0;
        int start = idx + 6;
        int end = json.indexOf("}", start);
        if (end == -1) end = json.indexOf(",", start);
        return Long.parseLong(json.substring(start, end).trim());
    }

    private Long extractUserId(String json) {
        int idx = json.indexOf("\"sub\":\"");
        if (idx == -1) {
            // Check without quotes
            idx = json.indexOf("\"sub\":");
            if (idx == -1) throw new IllegalArgumentException("Token missing sub");
            int start = idx + 6;
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Long.parseLong(json.substring(start, end).trim());
        }
        int start = idx + 7;
        int end = json.indexOf("\"", start);
        return Long.parseLong(json.substring(start, end));
    }
}

