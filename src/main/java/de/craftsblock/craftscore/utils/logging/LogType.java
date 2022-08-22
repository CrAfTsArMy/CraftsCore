package de.craftsblock.craftscore.utils.logging;

public enum LogType {

    INFO("[INFO / %t]: "),
    WARN("[WARN / %t]: "),
    ERROR("[ERROR / %t]: "),
    EXCEPTION("[FATAL ERROR / %t]: ");

    private final String prefix;

    LogType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix.replace("%t", Thread.currentThread().getName());
    }

}
