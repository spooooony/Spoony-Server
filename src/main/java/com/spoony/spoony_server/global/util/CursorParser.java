package com.spoony.spoony_server.global.util;

import com.spoony.spoony_server.adapter.dto.Cursor;

import java.time.LocalDateTime;

public class CursorParser {
    public static Cursor parse(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;

        Long zzimCount = null;
        LocalDateTime createdAt = null;

        String[] parts = cursor.split("\\|");
        for (String part : parts) {
            String[] kv = part.split(":");
            if (kv.length != 2) continue;

            String key = kv[0];
            String value = kv[1];

            switch (key) {
                case "zzimCount":
                    zzimCount = Long.parseLong(value);
                    break;
                case "createdAt":
                    createdAt = LocalDateTime.parse(value);
                    break;
            }
        }

        return new Cursor(zzimCount, createdAt);
    }
}
