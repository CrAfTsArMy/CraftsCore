package de.craftsarmy.craftscore.utils.logging;

public final class Logger {

    public static void i(String message) {
        log(LogType.INFO, message);
    }
    public static void w(String message) {
        log(LogType.WARN, message);
    }
    public static void e(String message) {
        log(LogType.ERROR, message);
    }
    public static void ex(String message) {
        log(LogType.EXCEPTION, message);
    }
    public static void log(LogType type, String message) {
        System.out.println(type.getPrefix() + message);
    }

}
