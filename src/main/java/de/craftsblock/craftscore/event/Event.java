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
 * @version 1.0.0
 * @since 3.6.16-SNAPSHOT
 */
public abstract class Event {

}
