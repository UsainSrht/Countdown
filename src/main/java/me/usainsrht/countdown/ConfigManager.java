package me.usainsrht.countdown;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class ConfigManager {
    private static final Path CONFIG_PATH = Paths.get("plugins", "MiniPlaceholders", "countdown.properties");
    private static final Path CONFIG_PATH_ALT = Paths.get("plugins", "miniplaceholders", "countdown.properties");

    private Properties properties;
    private long lastChecked;
    private long lastModified;
    private Path activePath;

    public ConfigManager() {
        this.activePath = CONFIG_PATH;
        if (!Files.exists(this.activePath) && Files.exists(CONFIG_PATH_ALT)) {
            this.activePath = CONFIG_PATH_ALT;
        }
        reloadIfNeeded(true);
    }

    public synchronized void reloadIfNeeded(boolean force) {
        long now = System.currentTimeMillis();
        if (!force && (now - lastChecked < 5000)) {
            return;
        }
        lastChecked = now;

        if (!Files.exists(activePath) && Files.exists(CONFIG_PATH_ALT)) {
            activePath = CONFIG_PATH_ALT;
        }

        if (Files.exists(activePath)) {
            try {
                long modified = Files.getLastModifiedTime(activePath).toMillis();
                if (force || modified > lastModified) {
                    lastModified = modified;
                    Properties props = new Properties();
                    try (InputStream in = Files.newInputStream(activePath)) {
                        props.load(in);
                        this.properties = props;
                    }
                }
            } catch (IOException e) {
                // Ignore
            }
        } else {
            // Create default
            Properties props = new Properties();
            props.setProperty("datetime",
                    LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            props.setProperty("format", "%dd %hh %mm %ss");
            props.setProperty("finishtext", "Countdown finished!");
            this.properties = props;

            try {
                Path parent = activePath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                try (OutputStream out = Files.newOutputStream(activePath)) {
                    props.store(out, "Countdown MiniPlaceholders Expansion configuration\n" +
                            "datetime: yyyy-MM-dd HH:mm:ss, ISO-8601, or epoch milliseconds\n" +
                            "format: remaining duration format (supports %d, %dd, %h, %hh, %m, %mm, %s, %ss)\n" +
                            "finishtext: text to display when countdown finishes");
                }
                lastModified = Files.getLastModifiedTime(activePath).toMillis();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public String getProperty(String key, String defaultValue) {
        reloadIfNeeded(false);
        return properties.getProperty(key, defaultValue);
    }
}
