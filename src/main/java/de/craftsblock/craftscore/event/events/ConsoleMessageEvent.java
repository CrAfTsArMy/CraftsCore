package de.craftsblock.craftscore.event.events;

import de.craftsblock.craftscore.event.Cancelable;
import de.craftsblock.craftscore.event.Event;

public class ConsoleMessageEvent extends Event implements Cancelable {

    private boolean cancelled = false;
    private final String message;

    public ConsoleMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

}
