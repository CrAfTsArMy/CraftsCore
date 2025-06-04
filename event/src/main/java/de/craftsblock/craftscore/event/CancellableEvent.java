package de.craftsblock.craftscore.event;

/**
 * An abstract base class for events that can be cancelled.
 * <p>
 * This class extends {@link Event} and implements {@link Cancellable}, providing
 * a mechanism to set and check the cancellation status of an event. When an event
 * is marked as cancelled, subsequent handlers may skip further processing depending
 * on the implementation of the event system.
 * </p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see Event
 * @see Cancellable
 * @since 3.7.62-SNAPSHOT
 */
public abstract class CancellableEvent extends Event implements Cancellable {

    private boolean cancelled = false;

    /**
     * Sets the cancelled flag for the event, indicating whether the event is cancelled or not.
     *
     * @param cancelled true to cancel the event, false to allow processing.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Checks if the event has been cancelled.
     *
     * @return true if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

}
