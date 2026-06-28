package me.usainsrht.countdown;

import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.provider.ExpansionProvider;
import io.github.miniplaceholders.api.provider.LoadRequirement;
import io.github.miniplaceholders.api.resolver.GlobalTagResolver;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.Context;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class CountdownExpansion implements ExpansionProvider {

    private final ConfigManager configManager = new ConfigManager();

    @Override
    public Expansion provideExpansion() {
        return Expansion.builder("countdown")
                .version("1.0.0")
                .author("UsainSrht")
                .globalPlaceholder("countdown", (queue, ctx) -> {
                    String defaultDatetimeStr = configManager.getProperty("datetime", "2026-12-31 23:59:59");
                    String defaultFormatStr = configManager.getProperty("format", "%dd:%hh:%mm:%ss");
                    String defaultFinishText = configManager.getProperty("finishtext", "Countdown finished!");

                    String datetimeStr = defaultDatetimeStr;
                    String formatStr = defaultFormatStr;
                    String finishTextStr = defaultFinishText;

                    if (queue.hasNext()) {
                        String arg = queue.pop().value();
                        if (!arg.isEmpty()) {
                            datetimeStr = arg;
                        }
                    }
                    if (queue.hasNext()) {
                        String arg = queue.pop().value();
                        if (!arg.isEmpty()) {
                            formatStr = arg;
                        }
                    }
                    if (queue.hasNext()) {
                        String arg = queue.pop().value();
                        if (!arg.isEmpty()) {
                            finishTextStr = arg;
                        }
                    }

                    Instant defaultInstant = parseDateTime(defaultDatetimeStr, Instant.EPOCH);
                    Instant targetInstant = parseDateTime(datetimeStr, defaultInstant);

                    Duration duration = Duration.between(Instant.now(), targetInstant);
                    String output;
                    if (duration.isNegative() || duration.isZero()) {
                        output = finishTextStr;
                    } else {
                        output = formatDuration(duration, formatStr);
                    }

                    return Tag.selfClosingInserting(ctx.deserialize(output));
                })
                .build();
    }

    @Override
    public LoadRequirement loadRequirement() {
        return LoadRequirement.none();
    }

    static Instant parseDateTime(String input, Instant defaultVal) {
        if (input == null || input.trim().isEmpty()) {
            return defaultVal;
        }
        input = input.trim();
        try {
            long value = Long.parseLong(input);
            if (value < 100000000000L) {
                return Instant.ofEpochSecond(value);
            } else {
                return Instant.ofEpochMilli(value);
            }
        } catch (NumberFormatException e) {
            // Not a number
        }

        try {
            return OffsetDateTime.parse(input).toInstant();
        } catch (Exception e) {
            // Ignore
        }

        try {
            return Instant.parse(input);
        } catch (Exception e) {
            // Ignore
        }

        try {
            return LocalDateTime.parse(input).atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            // Ignore
        }

        try {
            return LocalDate.parse(input).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            // Ignore
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(input, formatter).atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            // Ignore
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(input, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            // Ignore
        }

        return defaultVal;
    }

    static String formatDuration(Duration duration, String format) {
        long totalSeconds = duration.getSeconds();
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String result = format;

        // 1. Replace word placeholders first
        result = result.replace("%days%", String.valueOf(days));
        result = result.replace("%hours%", String.valueOf(hours));
        result = result.replace("%minutes%", String.valueOf(minutes));
        result = result.replace("%seconds%", String.valueOf(seconds));

        // 2. Replace padded placeholders next
        result = result.replace("%dd", String.format("%02d", days));
        result = result.replace("%hh", String.format("%02d", hours));
        result = result.replace("%mm", String.format("%02d", minutes));
        result = result.replace("%ss", String.format("%02d", seconds));

        // 3. Replace single character placeholders last
        result = result.replace("%d", String.valueOf(days));
        result = result.replace("%h", String.valueOf(hours));
        result = result.replace("%m", String.valueOf(minutes));
        result = result.replace("%s", String.valueOf(seconds));

        return result;
    }
}
