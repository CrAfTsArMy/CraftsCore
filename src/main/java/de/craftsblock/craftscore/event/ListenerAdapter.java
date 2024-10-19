package de.craftsblock.craftscore.event;

/**
 * The {@link ListenerAdapter} interface serves as a marker for any class that
 * wants to listen and respond to events within the system. Classes implementing
 * this interface are intended to be registered with an event manager or dispatcher
 * to handle specific events.
 *
 * <p>This interface does not define any methods; it acts purely as a tagging interface
 * to indicate that a class is capable of listening to events. The actual event handling
 * methods would be defined in the implementing class or managed through reflection
 * by the event dispatcher.</p>
 *
 * <p>Implementing {@link ListenerAdapter} allows a class to be registered to listen
 * for events, often in conjunction with an event management framework that invokes
 * the appropriate event-handling methods.</p>
 *
 * <p><b>Inspired by:</b> <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/event">Bukkit's Event System</a></p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @since 3.6.16-SNAPSHOT
 * @version 1.0.0
 */
public interface ListenerAdapter {
}
