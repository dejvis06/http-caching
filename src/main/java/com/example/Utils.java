package com.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

public class Utils {

    private Utils() {
    }

    // Parses String (RFC1123) -> Instant
    public static Instant parseRFC1123ToInstant(String ifModifiedSince) {
        return ZonedDateTime.parse(ifModifiedSince, DateTimeFormatter.RFC_1123_DATE_TIME)
                .toInstant();
    }

    // Formats Instant -> RFC1123 string
    public static String formatInstantToRFC1123(Instant instant) {
        return DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneId.of("GMT"))
                .format(instant);
    }

    // SHA-256 hashing
    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
