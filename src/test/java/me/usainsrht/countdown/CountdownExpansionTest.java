package me.usainsrht.countdown;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class CountdownExpansionTest {

    @Test
    public void testParseDateTimeEpochSeconds() {
        Instant defaultVal = Instant.EPOCH;
        Instant result = CountdownExpansion.parseDateTime("1719585029", defaultVal);
        assertEquals(1719585029L, result.getEpochSecond());
    }

    @Test
    public void testParseDateTimeEpochMillis() {
        Instant defaultVal = Instant.EPOCH;
        Instant result = CountdownExpansion.parseDateTime("1719585029000", defaultVal);
        assertEquals(1719585029L, result.getEpochSecond());
    }

    @Test
    public void testParseDateTimeISOInstant() {
        Instant defaultVal = Instant.EPOCH;
        Instant result = CountdownExpansion.parseDateTime("2026-06-28T15:30:29Z", defaultVal);
        assertEquals(Instant.parse("2026-06-28T15:30:29Z"), result);
    }

    @Test
    public void testParseDateTimeLocalDateTime() {
        Instant defaultVal = Instant.EPOCH;
        String dateStr = "2026-06-28 15:30:29";
        Instant expected = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Instant result = CountdownExpansion.parseDateTime(dateStr, defaultVal);
        assertEquals(expected, result);
    }

    @Test
    public void testParseDateTimeLocalDate() {
        Instant defaultVal = Instant.EPOCH;
        String dateStr = "2026-06-28";
        Instant expected = LocalDateTime
                .parse(dateStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Instant result = CountdownExpansion.parseDateTime(dateStr, defaultVal);
        assertEquals(expected, result);
    }

    @Test
    public void testParseDateTimeInvalidFallback() {
        Instant defaultVal = Instant.parse("2026-12-31T23:59:59Z");
        Instant result = CountdownExpansion.parseDateTime("invalid-date-format", defaultVal);
        assertEquals(defaultVal, result);
    }

    @Test
    public void testFormatDurationNonPadded() {
        // 2 days, 3 hours, 4 minutes, 5 seconds
        Duration duration = Duration.ofSeconds(2 * 86400 + 3 * 3600 + 4 * 60 + 5);
        String formatted = CountdownExpansion.formatDuration(duration, "%d days, %h hours, %m mins, %s secs");
        assertEquals("2 days, 3 hours, 4 mins, 5 secs", formatted);
    }

    @Test
    public void testFormatDurationPadded() {
        // 2 days, 3 hours, 4 minutes, 5 seconds
        Duration duration = Duration.ofSeconds(2 * 86400 + 3 * 3600 + 4 * 60 + 5);
        String formatted = CountdownExpansion.formatDuration(duration, "%dd:%hh:%mm:%ss");
        assertEquals("02:03:04:05", formatted);
    }

    @Test
    public void testFormatDurationWords() {
        // 1 day, 12 hours, 30 minutes, 15 seconds
        Duration duration = Duration.ofSeconds(1 * 86400 + 12 * 3600 + 30 * 60 + 15);
        String formatted = CountdownExpansion.formatDuration(duration, "%days% days, %hours% hours");
        assertEquals("1 days, 12 hours", formatted);
    }
}
