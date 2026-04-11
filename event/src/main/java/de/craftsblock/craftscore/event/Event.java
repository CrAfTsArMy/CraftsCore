package de.craftsblock.craftscore.event;

/**
 * The {@link Event} class serves as the base class for all event types in the system.
 * Any specific event that needs to be handled by an event listener should extend
 * this abstract class.
 *
 * <p>By extending {@link Event}, custom events can carry additional data and
 * behavior, depending on the use case. The event system or dispatcher typically
 * uses instances of this class (or its subclasses) to notify registered listeners
 * when certain actions occur within the application.</p>
 *
 * <p><b>Inspired by:</b> <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/event">Bukkit's Event System</a></p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @since 3.6.16-SNAPSHOT
 */
public abstract class Event {

    private boolean async = false;

    /**
     * Determines whether this event type is allowed to be executed asynchronously.
     * <p>
     * Subclasses may override this method to restrict an event to synchronous
     * execution only. If overridden to return {@code false}, the event system
     * will prevent dispatching this event on asynchronous threads.
     *
     * @return {@code true} if the event may be executed asynchronously,
     * otherwise {@code false}
     * @since 3.8.13
     */
    protected boolean isAsyncAllowed() {
        return true;
    }

    /**
     * Ensures that this event is allowed to be executed asynchronously.
     * <p>
     * This method is used by the event dispatcher to validate whether the event
     * supports asynchronous execution before it is fired. If asynchronous execution
     * is not permitted, an exception is thrown.
     *
     * @throws IllegalStateException if this event does not allow async execution
     * @since 3.8.13
     */
    void ensureAsyncAllowed() {
        if (isAsyncAllowed()) {
            return;
        }

        throw new IllegalStateException(getClass().getName() + " can not be thrown async!");
    }

    /**
     * Marks this event as being executed in an asynchronous context.
     * <p>
     * This method is typically called by the event dispatcher when an event
     * is being fired from a non-main thread or asynchronous execution pipeline.
     *
     * @since 3.8.13
     */
    void markAsync() {
        this.async = true;
    }

    /**
     * Returns whether this event is currently being executed asynchronously.
     * <p>
     * This can be used by listeners to adjust their behavior depending on
     * threading context requirements.
     *
     * @return {@code true} if the event is executed asynchronously, otherwise {@code false}
     * @since 3.8.13
     */
    public final boolean isAsync() {
        return async;
    }

}
