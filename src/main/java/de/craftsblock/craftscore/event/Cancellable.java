package de.craftsblock.craftscore.event;

/**
 * The {@link Cancellable} interface is used to mark events that can be cancelled.
 * Events that implement this interface provide the ability to stop further
 * propagation or prevent default behavior by setting a cancellation state.
 *
 * <p>Classes implementing this interface must provide implementations for two methods:</p>
 * <ul>
 *   <li>{@link #setCancelled(boolean)} - Sets the cancellation state of the event.</li>
 *   <li>{@link #isCancelled()} - Returns whether the event is currently cancelled.</li>
 * </ul>
 *
 * <p>Once an event is cancelled, other listeners may still be notified, but the main action
 * of the event (if any) will not proceed unless explicitly handled otherwise.</p>
 *
 * <p><b>Inspired by:</b> <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/event">Bukkit's Event System</a></p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 3.6.16-SNAPSHOT
 */
public interface Cancellable {

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled {@code true} to cancel the event, {@code false} to allow it to proceed.
     */
    void setCancelled(boolean cancelled);

    /**
     * Returns whether the event has been cancelled.
     *
     * @return {@code true} if the event is cancelled, {@code false} otherwise.
     */
    boolean isCancelled();

}
