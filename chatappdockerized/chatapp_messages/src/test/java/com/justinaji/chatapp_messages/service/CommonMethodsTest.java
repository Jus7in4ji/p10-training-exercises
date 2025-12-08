package com.justinaji.chatapp_messages.service;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class CommonMethodsTest {

    @Test
    void testFormatTimestamp_TodaySameYear_UTC() {
        // create a timestamp for current date and time
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        String result = CommonMethods.formatTimestamp(timestamp, "UTC");

        // expected format = hh:mm:ss AM/PM (today case)
        String expected = timestamp.toInstant()
            .atZone(java.time.ZoneId.of("UTC"))
            .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));

        assertEquals(expected, result);
    }

    @Test
    void testFormatTimestamp_DifferentDaySameYear() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(2);
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        String result = CommonMethods.formatTimestamp(timestamp, "Asia/Kolkata");

        String expected = dateTime.atZone(java.time.ZoneId.of("Asia/Kolkata"))
                                  .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM | hh:mm:ss a"));

        assertEquals(expected, result);
    }

    @Test
    void testFormatTimestamp_DifferentYear() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 5, 10, 15, 30);
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        String result = CommonMethods.formatTimestamp(timestamp, "Asia/Kolkata");

        String expected = dateTime.atZone(java.time.ZoneId.of("Asia/Kolkata"))
                                  .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy | hh:mm:ss a"));

        assertEquals(expected, result);
    }

    @Test
    void testFormatTimestamp_InvalidTimezone() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        String result = CommonMethods.formatTimestamp(timestamp, "INVALID_ZONE");

        // Should fall back to UTC → today's case
        String expected = timestamp.toInstant()
                                   .atZone(java.time.ZoneId.of("UTC"))
                                   .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));

        assertEquals(expected, result);
    }
}
