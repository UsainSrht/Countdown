# Countdown Expansion for MiniPlaceholders

A dynamic countdown expansion for [MiniPlaceholders](https://github.com/MiniPlaceholders/MiniPlaceholders) that allows you to display highly customizable countdowns on your Minecraft server using Adventure MiniMessage.

## Features

- **Flexible Date/Time Formats**: Supports multiple target datetime formats:
  - **ISO-8601 Instant**: `2026-12-31T23:59:59Z`
  - **Local Date & Time**: `2026-06-28 15:30:29` (uses server timezone)
  - **Local Date**: `2026-06-28` (midnight of the specified date)
  - **Epoch Milliseconds**: `1719585029000`
  - **Epoch Seconds**: `1719585029`
- **Fully Customizable Duration Formatting**: Supports non-padded, padded, and word placeholders.
- **Custom Finish Text**: Specify what message to show once the countdown has ended.
- **Global Configuration**: Configure default values in a properties file so you don't need to specify parameters every time.
- **Argument Fallbacks**: Easily override specific parameters while falling back to defaults for others by leaving arguments blank (e.g., `<countdown_countdown::%d hours:Ended!>`).

---

## Installation

1. Download or build the expansion jar file (`countdown-1.0.0.jar`).
2. Place the jar file inside your server's `plugins/MiniPlaceholders/` directory.
3. Restart or reload the server.

---

## Configuration

Upon the first run, the expansion will generate a configuration file at `plugins/MiniPlaceholders/countdown.properties` containing the following default values:

```properties
# Countdown MiniPlaceholders Expansion configuration
# datetime: yyyy-MM-dd HH:mm:ss, ISO-8601, or epoch milliseconds
# format: remaining duration format (supports %d, %dd, %h, %hh, %m, %mm, %s, %ss)
# finishtext: text to display when countdown finishes

datetime=2026-12-31 23:59:59
format=%dd %hh %mm %ss
finishtext=Countdown finished!
```

---

## Placeholders & Usage

The main placeholder provided is:

### `<countdown_countdown>`
Displays the countdown using the default configuration settings.

### Custom Arguments Syntax:
```
<countdown_countdown:[datetime]:[format]:[finishtext]>
```
Each argument is optional. If left blank or omitted, it falls back to the default configuration value.

#### Format Placeholders:
When writing your format string, you can use the following placeholders:

| Placeholder | Output Type | Example (Value = 5) |
|-------------|-------------|---------------------|
| `%d`        | Single days | `5`                 |
| `%dd`       | Padded days | `05`                |
| `%days%`    | Word days   | `5`                 |
| `%h`        | Single hours| `5`                 |
| `%hh`       | Padded hours| `05`                |
| `%hours%`   | Word hours  | `5`                 |
| `%m`        | Single mins | `5`                 |
| `%mm`       | Padded mins | `05`                |
| `%minutes%` | Word mins   | `5`                 |
| `%s`        | Single secs | `5`                 |
| `%ss`       | Padded secs | `05`                |
| `%seconds%` | Word secs   | `5`                 |

---

### Examples

1. **Default Countdown**:
   - Tag: `<countdown_countdown>`
   - Output: `02d:04h:15m:30s` (depending on default format and target time)

2. **Custom Target Time with Default Format**:
   - Tag: `<countdown_countdown:2026-12-31T23:59:59Z>`
   - Output: Remaining time using the format defined in `countdown.properties`.

3. **Custom Format & Custom Finished Text**:
   - Tag: `<countdown_countdown:2026-12-31 23:59:59:%d days and %h hours:The Event has Started!>`
   - Output: `15 days and 3 hours` (or `The Event has Started!` if elapsed).

4. **Skipping Arguments (Using Fallbacks)**:
   - Tag: `<countdown_countdown::%h hours remaining:%finishtext%>`
   - Output: Overrides the format to show only hours, while keeping the default datetime and default finish text from the config.

---

## Building from Source

To compile and package the expansion yourself:

### Requirements:
- Java 17 or higher
- Maven 3

### Command:
```bash
mvn clean package
```
The compiled jar will be created in the `target/` directory:
- `target/countdown-1.0.0.jar`
