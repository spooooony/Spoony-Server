package com.spoony.spoony_server.adapter.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public record Cursor(Long zzimCount, LocalDateTime createdAt) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    public String toCursorString() {
        StringBuilder sb = new StringBuilder();
        if (zzimCount != null) {
            sb.append("zzimCount:").append(zzimCount);
        }
        if (createdAt != null) {
            if (!sb.isEmpty()) sb.append("|");
            sb.append("createdAt:")
                    .append(createdAt.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return sb.toString();
    }

    public static Cursor fromCursorString(String raw) {
        if (raw == null || raw.isEmpty()) return null;

        Long zzimCount = null;
        LocalDateTime createdAt = null;

        String[] parts = raw.split("\\|");
        for (String part : parts) {
            if (part.startsWith("zzimCount:")) {
                try {
                    zzimCount = Long.parseLong(part.substring("zzimCount:".length()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid zzimCount value in cursor: " + part, e);
                }
            } else if (part.startsWith("createdAt:")) {
                String datetimeStr = part.substring("createdAt:".length());
                try {
                    // ISO_LOCAL_DATE_TIME 포맷만 사용
                    createdAt = LocalDateTime.parse(datetimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid createdAt value in cursor: " + datetimeStr, e);
                }
            }
        }

        return new Cursor(zzimCount, createdAt);
    }

}
