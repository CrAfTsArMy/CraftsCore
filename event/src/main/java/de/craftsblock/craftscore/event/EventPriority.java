package de.craftsblock.craftscore.event;

/**
 * The {@link EventPriority} enum defines the different levels of priority
 * for handling events in the event system.
 *
 * <p><b>Inspired by:</b> <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/event">Bukkit's Event System</a></p>
 *
 * <p>Event priorities allow the system to determine the order in which
 * event listeners are called when an event is fired. Listeners with a higher
 * priority are invoked later than those with a lower priority, providing a way
 * to control the flow of event handling.</p>
 *
 * <p>Priorities are arranged in the following order (from lowest to highest):</p>
 * <ul>
 *   <li>{@link #LOWEST} - The event handler will be called first, allowing it
 *       to handle the event before any other listeners.</li>
 *   <li>{@link #LOW} - The handler will be called early, but after those with
 *       the {@code LOWEST} priority.</li>
 *   <li>{@link #NORMAL} - The default priority level, used when no other
 *       priority is specified.</li>
 *   <li>{@link #HIGH} - The handler will be called after {@link #NORMAL}
 *       priority handlers, typically for more important logic.</li>
 *   <li>{@link #HIGHEST} - The handler will be called last, before
 *       {@code MONITOR} listeners, for final handling.</li>
 *   <li>{@link #MONITOR} - This priority is reserved for listeners that only
 *       monitor the event without modifying it. These listeners are called after
 *       all other handlers and cannot alter the outcome of the event.</li>
 * </ul>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 3.6.16-SNAPSHOT
 */
public enum EventPriority {

    /**
     * The lowest priority, indicating the listener should be called first.
     */
    LOWEST,

    /**
     * A low priority, indicating the listener should be called early in the event processing.
     */
    LOW,

    /**
     * The default, normal priority for event listeners.
     */
    NORMAL,

    /**
     * A high priority, indicating the listener should be called after the normal priority handlers.
     */
    HIGH,

    /**
     * The highest priority, indicating the listener should be called last before the monitor.
     */
    HIGHEST,

    /**
     * A priority for monitoring the event without modifying it.
     * This priority should only be used for listeners that need to observe the event's final state.
     */
    MONITOR

}
