package de.craftsblock.craftscore.event;

/**
 * Represents an abstract base class for events that can be cancelled.
 * <p>
 * This class provides a mechanism to mark events as cancelled, which can be checked
 * to determine whether further processing should occur.
 * </p>
 *
 * <p>Subclasses that extend {@link CancellableEvent} inherit this cancellable behavior.</p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see Cancellable
 * @version 1.0.0
 * @since 3.7.62-SNAPSHOT
 */
public abstract class CancellableEvent implements Cancellable {

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
