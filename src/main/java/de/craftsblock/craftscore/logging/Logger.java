package de.craftsblock.craftscore.logging;

/**
 * The Logger class provides simple logging functionality for various log types.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 3.2-SNAPSHOT
 * @deprecated This class and its methods have been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
 * There is no specific alternative implementation provided in this version.
 * Developers are encouraged to avoid using these deprecated elements.
 */
@Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
public final class Logger {

    /**
     * Logs an informational message with the specified content.
     *
     * @param message The message content to be logged.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static void i(String message) {
        log(LogType.INFO, message);
    }

    /**
     * Logs a warning message with the specified content.
     *
     * @param message The message content to be logged.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static void w(String message) {
        log(LogType.WARN, message);
    }

    /**
     * Logs an error message with the specified content.
     *
     * @param message The message content to be logged.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static void e(String message) {
        log(LogType.ERROR, message);
    }

    /**
     * Logs an exception message with the specified content.
     *
     * @param message The message content to be logged.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static void ex(String message) {
        log(LogType.EXCEPTION, message);
    }

    /**
     * Logs a message with the specified log type and content.
     *
     * @param type    The log type.
     * @param message The message content to be logged.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static void log(LogType type, String message) {
        System.out.println(type.getPrefix() + message);
    }

}
