package de.craftsblock.craftscore.logging;

/**
 * The LogType enum represents different types of log entries.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 3.2-SNAPSHOT
 * @deprecated This enum has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
 * There is no specific alternative implementation provided in this version.
 * Developers are encouraged to avoid using this deprecated enum and switch to recommended replacements.
 */
@Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
public enum LogType {

    /**
     * Informational log type.
     */
    INFO("[INFO / %t]: "),
    /**
     * Warning log type.
     */
    WARN("[WARN / %t]: "),
    /**
     * Error log type.
     */
    ERROR("[ERROR / %t]: "),
    /**
     * Exception log type.
     */
    EXCEPTION("[FATAL ERROR / %t]: ");

    private final String prefix;

    /**
     * Constructs a LogType enum constant with the specified prefix.
     *
     * @param prefix The prefix to be used for log entries of this type.
     * @deprecated This constructor has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated constructor and switch to recommended replacements.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    LogType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the prefix associated with this log type.
     * The "%t" placeholder in the prefix will be replaced by the current thread's name.
     *
     * @return The log prefix with the thread name.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public String getPrefix() {
        return prefix.replace("%t", Thread.currentThread().getName());
    }

}
