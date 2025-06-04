package de.craftsblock.craftscore.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link EventHandler} annotation is used to mark methods as event handlers
 * within an event-driven system. Methods annotated with {@code @EventHandler}
 * will be registered to listen for specific events, which are dispatched by an
 * event manager or dispatcher.
 *
 * <p>This annotation also allows specifying an optional {@link EventPriority}
 * to determine the order in which the event handlers are called. By default,
 * the priority is set to {@link EventPriority#NORMAL}, but it can be adjusted
 * to ensure that certain event listeners are invoked earlier or later in the
 * event handling process.</p>
 *
 * <p>Methods marked with {@code @EventHandler} should follow these conventions:</p>
 * <ul>
 *   <li>The method must have a single parameter, which is the event to be handled.</li>
 *   <li>The method should not return a value (it should be {@code void}).</li>
 *   <li>Event dispatchers will automatically detect and invoke these methods based on the event type.</li>
 * </ul>
 *
 * <p><b>Inspired by:</b> <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/event">Bukkit's Event System</a></p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @see EventPriority
 * @since 3.6.16-SNAPSHOT
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    EventPriority priority() default EventPriority.NORMAL;

}
