package de.craftsblock.craftscore.event;

import de.craftsblock.craftscore.utils.Utils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link ListenerRegistry} class is responsible for managing event listeners
 * and dispatching events to registered handlers in an event-driven system. It
 * maintains a mapping of event types to listener methods and ensures that events
 * are handled in the correct order based on their {@link EventPriority}.
 *
 * <p>This class allows listeners to register and unregister event handler methods
 * dynamically. It also supports event queuing for deferred event dispatch.</p>
 *
 * <p>The registry uses reflection to detect and invoke methods annotated with
 * {@link EventHandler} in the provided listener objects.</p>
 *
 * <p><b>Inspired by:</b> <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/event">Bukkit's Event System</a></p>
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.1.5
 * @since 3.6.16-SNAPSHOT
 */
public class ListenerRegistry {

    private final ConcurrentHashMap<Class<? extends Event>, EnumMap<EventPriority, List<Listener>>> data = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Short, ConcurrentLinkedQueue<Event>> channelQueues = new ConcurrentHashMap<>();

    /**
     * Registers an event listener. All methods in the provided {@code ListenerAdapter}
     * object annotated with {@link EventHandler} are registered to handle events.
     *
     * @param adapter The listener containing event handler methods.
     */
    public void register(ListenerAdapter adapter) {
        for (Method method : Utils.getMethodsByAnnotation(adapter.getClass(), EventHandler.class))
            try {
                Class<? extends Event> event = getEventTypeOrThrow(method);
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                data.computeIfAbsent(event, p -> new EnumMap<>(EventPriority.class))
                        .computeIfAbsent(eventHandler.priority(), e -> new ArrayList<>())
                        .add(new Listener(method, adapter, eventHandler.priority(), eventHandler.ignoreCancelled()));
            } catch (Exception e) {
                throw new RuntimeException("Could not register handler %s#%s(%s)!".formatted(
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(), String.join(", ", Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toList())
                ), e);
            }
    }

    /**
     * Unregisters an event listener, removing all of its registered event handler methods
     * from the registry.
     *
     * @param adapter The listener whose event handlers should be unregistered.
     */
    public void unregister(ListenerAdapter adapter) {
        for (Method method : Utils.getMethodsByAnnotation(adapter.getClass(), EventHandler.class))
            try {
                Class<? extends Event> event = getEventTypeOrThrow(method);
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                if (!data.containsKey(event)) continue;

                EnumMap<EventPriority, List<Listener>> listeners = data.get(event);
                if (!listeners.containsKey(eventHandler.priority())) continue;
                listeners.get(eventHandler.priority()).removeIf(listener -> listener.method().equals(method));

                // Remove the event priority if there are no left listeners
                if (listeners.get(eventHandler.priority()).isEmpty())
                    listeners.remove(eventHandler.priority());
                else
                    // Continue as there are more listeners remaining
                    continue;

                // Remove the event type if no listeners are left
                if (listeners.isEmpty()) data.remove(event);
            } catch (Exception e) {
                throw new RuntimeException("Could not unregister handler %s#%s(%s)!".formatted(
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(), String.join(", ", Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toList())
                ), e);
            }
    }

    /**
     * Retrieves the event type for which the handler is listening.
     *
     * @param method The method which handles a given event type.
     * @return The type of the event the handler listens for.
     * @since 3.8.7
     */
    private static @NotNull Class<? extends Event> getEventTypeOrThrow(Method method) {
        String exception = "The method %s is provided with %s but does not include %s as the first argument!".formatted(
                method.getName(), EventHandler.class.getName(), Event.class.getName()
        );

        if (method.getParameterCount() <= 0)
            throw new IllegalStateException(exception);

        Class<?> parameter = method.getParameters()[0].getType();
        if (!Event.class.isAssignableFrom(parameter))
            throw new IllegalStateException(exception);

        return parameter.asSubclass(Event.class);
    }

    /**
     * Checks if the given {@link ListenerAdapter} is registered.
     * This class is a wrapper for {@link ListenerRegistry#isRegistered(Class)}.
     *
     * @param listenerAdapter The {@link ListenerAdapter} to check.
     * @return {@code true} when the {@link ListenerAdapter} was registered, {@code false} otherwise.
     */
    public boolean isRegistered(ListenerAdapter listenerAdapter) {
        return isRegistered(listenerAdapter.getClass());
    }

    /**
     * Checks if the given class representation of the {@link ListenerAdapter} is registered.
     *
     * @param type The class representation of the {@link ListenerAdapter} to check.
     * @return {@code true} when the {@link ListenerAdapter} was registered, {@code false} otherwise.
     */
    public boolean isRegistered(Class<? extends ListenerAdapter> type) {
        if (data.isEmpty()) return false;

        return data.values().stream()
                .filter(map -> !map.isEmpty())
                .flatMap(map -> map.values().stream())
                .filter(list -> !list.isEmpty())
                .flatMap(List::stream)
                .map(Listener::self)
                .anyMatch(type::isInstance);
    }

    /**
     * Calls the event by invoking all registered listeners for the given event type
     * in order of their {@link EventPriority}.
     *
     * @param event The event to be dispatched.
     * @throws InvocationTargetException If the listener method cannot be invoked.
     * @throws IllegalAccessException    If the listener method is inaccessible.
     */
    public void call(Event event) throws InvocationTargetException, IllegalAccessException {
        call(event, event.getClass());
    }

    /**
     * Calls the event by invoking all registered listeners for the given event type
     * in order of their {@link EventPriority}.
     *
     * @param event The event to be dispatched.
     * @param type  The type of the event listener to call.
     * @throws InvocationTargetException If the listener method cannot be invoked.
     * @throws IllegalAccessException    If the listener method is inaccessible.
     */
    private void call(Event event, Class<? extends Event> type) throws InvocationTargetException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        Class<? extends Event> superClass = (Class<? extends Event>) type.getSuperclass();
        if (superClass != null && Event.class.isAssignableFrom(superClass))
            call(event, superClass);

        if (!this.data.containsKey(type))
            return;

        EnumMap<EventPriority, List<Listener>> data = this.data.get(type);
        if (data.isEmpty()) return;

        for (EventPriority priority : data.keySet()) {
            List<Listener> listeners = data.get(priority);
            if (listeners.isEmpty()) continue;

            for (Listener tile : listeners) {
                if (event instanceof Cancellable cancellable && cancellable.isCancelled() && !tile.ignoreCancelled())
                    continue;

                Method method = tile.method();
                try {
                    method.setAccessible(true);
                    method.invoke(tile.self, event);
                } finally {
                    method.setAccessible(false);
                }
            }
        }
    }

    /**
     * Queues an event for deferred processing in the default channel (channel 0).
     *
     * @param event The event to be queued.
     */
    public void queueCall(Event event) {
        queueCall((short) 0, event);
    }

    /**
     * Queues an event for deferred processing in a specified channel.
     *
     * @param channel The channel ID to queue the event in.
     * @param event   The event to be queued.
     */
    public void queueCall(Short channel, Event event) {
        channelQueues.computeIfAbsent(channel, i -> new ConcurrentLinkedQueue<>()).add(event);
    }

    /**
     * Processes and dispatches all queued events in the default channel (channel 0).
     *
     * @throws InvocationTargetException If a listener method cannot be invoked.
     * @throws IllegalAccessException    If a listener method is inaccessible.
     */
    public void callQueued() throws InvocationTargetException, IllegalAccessException {
        callQueued((short) 0);
    }

    /**
     * Processes and dispatches all queued events across all channels.
     *
     * @throws InvocationTargetException If a listener method cannot be invoked.
     * @throws IllegalAccessException    If a listener method is inaccessible.
     */
    public void callAllQueued() throws InvocationTargetException, IllegalAccessException {
        for (Short channel : channelQueues.keySet())
            callQueued(channel);
    }

    /**
     * Processes and dispatches all queued events in a specific channel.
     *
     * @param channel The channel ID to process queued events from.
     * @throws InvocationTargetException If a listener method cannot be invoked.
     * @throws IllegalAccessException    If a listener method is inaccessible.
     */
    public void callQueued(Short channel) throws InvocationTargetException, IllegalAccessException {
        if (!channelQueues.containsKey(channel)) return;

        ConcurrentLinkedQueue<Event> queue = channelQueues.get(channel);
        for (Event event : queue) {
            call(event);
            queue.remove(event);
        }

        if (queue.isEmpty()) channelQueues.remove(channel);
    }

    /**
     * Internal record representing a registered listener.
     *
     * @param method          The method to be invoked for the event.
     * @param self            The instance of the listener object.
     * @param priority        The priority of the event handler.
     * @param ignoreCancelled Whether the listener is still performed even when the event is already cancelled.
     */
    @ApiStatus.Internal
    private record Listener(Method method, Object self, EventPriority priority, boolean ignoreCancelled) {
    }

}
