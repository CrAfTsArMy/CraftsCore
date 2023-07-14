package de.craftsblock.craftscore.event;

public enum EventPriority {

    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR;

    public static EventPriority next(EventPriority current) {
        return switch (current) {
            case LOWEST -> LOW;
            case LOW -> NORMAL;
            case NORMAL -> HIGH;
            case HIGH -> HIGHEST;
            case HIGHEST -> MONITOR;
            default -> null;
        };
    }

}
