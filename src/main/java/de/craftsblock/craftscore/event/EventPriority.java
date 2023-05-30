package de.craftsblock.craftscore.event;

public enum EventPriority {

    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR;

    public static EventPriority next(EventPriority current) {
        switch (current) {
            case LOWEST:
                return LOW;
            case LOW:
                return NORMAL;
            case NORMAL:
                return HIGH;
            case HIGH:
                return HIGHEST;
            case HIGHEST:
                return MONITOR;
            default:
                return null;
        }
    }

}
