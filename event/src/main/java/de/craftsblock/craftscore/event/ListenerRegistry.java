package de.craftsblock.craftscore.event;

import de.craftsblock.craftscore.event.listener.DirectListener;
import de.craftsblock.craftscore.event.listener.Listener;
import de.craftsblock.craftscore.event.listener.ReflectionListener;
import de.craftsblock.craftscore.event.queue.CallQueue;
import de.craftsblock.craftscore.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
 * @since 3.6.16-SNAPSHOT
 */
public class ListenerRegistry {

    private static final ListenerRegistry GLOBAL = new ListenerRegistry();
    private static final EventPriority[] PRIORITIES;
    private static final EventPriority[] REVERSED_PRIORITIES;

    static {
        List<EventPriority> priorities = Arrays.asList(EventPriority.values());
        PRIORITIES = priorities.toArray(EventPriority[]::new);

        Collections.reverse(priorities);
        REVERSED_PRIORITIES = priorities.toArray(EventPriority[]::new);
    }

    private final @NotNull ExecutorService executorService;
    private final @NotNull CallQueue callQueue;

    private final Set<ListenerAdapter> listenerIndex = ConcurrentHashMap.newKeySet();

    private final Map<Class<? extends Event>, Map<EventPriority, List<Listener>>> registeredListeners = new HashMap<>();
    private final ClassValue<Listener> bakedListeners = new ClassValue<>() {
        @Override
        @SuppressWarnings("unchecked")
        protected Listener computeValue(@NotNull Class<?> type) {
            return bake((Class<? extends Event>) type);
        }
    };

    /**
     * Creates a new {@link ListenerRegistry} using a cached thread pool
     * as its default asynchronous execution strategy.
     */
    public ListenerRegistry() {
        this(Executors.newCachedThreadPool());
    }

    /**
     * Creates a new {@link ListenerRegistry} using the provided
     * {@link ExecutorService} for asynchronous event dispatching.
     *
     * @param executorService The executor service used to execute asynchronous event calls.
     * @since 3.8.13
     */
    public ListenerRegistry(@NotNull ExecutorService executorService) {
        this.executorService = executorService;
        this.callQueue = new CallQueue(this);
    }

    /**
     * Registers an event listener. All methods in the provided {@code ListenerAdapter}
     * object annotated with {@link EventHandler} are registered to handle events.
     *
     * @param adapter The listener containing event handler methods.
     */
    public void register(@NotNull ListenerAdapter adapter) {
        Set<Class<? extends Event>> changedEvents = new HashSet<>();
        List<Method> methods = Utils.getMethodsByAnnotation(adapter.getClass(), EventHandler.class);
        for (Method method : methods) {
            try {
                Class<? extends Event> event = getEventTypeOrThrow(method);
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                Listener listener = new ReflectionListener(
                        event, method, adapter,
                        eventHandler.priority(),
                        eventHandler.ignoreWhenCancelled()
                );

                registeredListeners
                        .computeIfAbsent(event, p -> new EnumMap<>(EventPriority.class))
                        .computeIfAbsent(eventHandler.priority(), e -> new CopyOnWriteArrayList<>())
                        .add(listener);

                changedEvents.add(event);
            } catch (Exception e) {
                throw new RuntimeException("Could not register handler %s#%s(%s)!".formatted(
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        Arrays.toString(method.getParameterTypes())
                ), e);
            }
        }

        listenerIndex.add(adapter);
        bakeAll(changedEvents);
    }

    /**
     * Registers a functional event listener for the given event type using
     * default priority {@link EventPriority#NORMAL} and without ignoring cancelled events.
     *
     * @param eventType The class type of the event to listen for.
     * @param consumer  The consumer that will handle the event.
     * @param <T>       The type of the event.
     * @since 3.8.13
     */
    public <T extends Event> void register(@NotNull Class<T> eventType, @NotNull Consumer<T> consumer) {
        this.register(eventType, consumer, EventPriority.NORMAL, false);
    }

    /**
     * Registers a functional event listener for the given event type using
     * default priority {@link EventPriority#NORMAL}.
     *
     * @param eventType           The class type of the event to listen for.
     * @param consumer            The consumer that will handle the event.
     * @param ignoreWhenCancelled Whether the listener should ignore cancelled events.
     * @param <T>                 The type of the event.
     * @since 3.8.13
     */
    public <T extends Event> void register(@NotNull Class<T> eventType, @NotNull Consumer<T> consumer,
                                           boolean ignoreWhenCancelled) {
        this.register(eventType, consumer, EventPriority.NORMAL, ignoreWhenCancelled);
    }

    /**
     * Registers a functional event listener for the given event type without
     * specifying cancellation behavior.
     *
     * @param eventType The class type of the event to listen for.
     * @param consumer  The consumer that will handle the event.
     * @param priority  The priority at which the listener should be executed.
     * @param <T>       The type of the event.
     * @since 3.8.13
     */
    public <T extends Event> void register(@NotNull Class<T> eventType, @NotNull Consumer<T> consumer,
                                           @NotNull EventPriority priority) {
        this.register(eventType, consumer, priority, false);
    }

    /**
     * Registers a functional event listener for the given event type.
     *
     * @param eventType           The class type of the event to listen for.
     * @param consumer            The consumer that will handle the event.
     * @param priority            The priority at which the listener should be executed.
     * @param ignoreWhenCancelled Whether the listener should ignore cancelled events.
     * @param <T>                 The type of the event.
     * @since 3.8.13
     */
    public <T extends Event> void register(@NotNull Class<T> eventType, @NotNull Consumer<T> consumer,
                                           @NotNull EventPriority priority, boolean ignoreWhenCancelled) {
        Listener listener = new DirectListener<>(eventType, consumer, priority, ignoreWhenCancelled);
        registeredListeners
                .computeIfAbsent(eventType, p -> new EnumMap<>(EventPriority.class))
                .computeIfAbsent(priority, e -> new CopyOnWriteArrayList<>())
                .add(listener);

        bakeAll(Collections.singleton(eventType));
    }

    /**
     * Unregisters an event listener, removing all of its registered event handler methods
     * from the registry.
     *
     * @param adapter The listener whose event handlers should be unregistered.
     */
    public void unregister(@NotNull ListenerAdapter adapter) {
        Set<Class<? extends Event>> changedEvents = new HashSet<>();
        List<Method> methods = Utils.getMethodsByAnnotation(adapter.getClass(), EventHandler.class);
        for (Method method : methods) {
            try {
                Class<? extends Event> event = getEventTypeOrThrow(method);
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);

                Map<EventPriority, List<Listener>> map = registeredListeners.get(event);
                if (map == null) {
                    continue;
                }

                List<Listener> listeners = map.get(eventHandler.priority());
                if (listeners == null) {
                    continue;
                }

                changedEvents.add(event);
                listeners.removeIf(listener ->
                        listener instanceof ReflectionListener rl &&
                                rl.getMethod().equals(method) &&
                                rl.getOwner() == adapter
                );

                if (listeners.isEmpty()) {
                    map.remove(eventHandler.priority());
                }

                if (map.isEmpty()) {
                    registeredListeners.remove(event);
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not unregister handler %s#%s(%s)!".formatted(
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        Arrays.toString(method.getParameterTypes())
                ), e);
            }
        }

        listenerIndex.remove(adapter);
        bakeAll(changedEvents);
    }

    /**
     * Invalidates and rebakes all cached listener chains for the given event types.
     * <p>
     * This ensures that any structural changes in the listener registry are properly
     * reflected in the internal execution order cache, including inherited event types.
     *
     * @param targetEvents The set of event types that have changed and require rebaking.
     * @since 3.8.13
     */
    @SuppressWarnings("unchecked")
    private void bakeAll(Collection<Class<? extends Event>> targetEvents) {
        Set<Class<? extends Event>> rebakeTypes = new HashSet<>();
        for (Class<? extends Event> type : targetEvents) {
            for (Class<?> current = type;
                 current != null && Event.class.isAssignableFrom(current);
                 current = current.getSuperclass()) {

                rebakeTypes.add((Class<? extends Event>) current);
            }
        }

        for (Class<? extends Event> type : rebakeTypes) {
            bakedListeners.remove(type);
        }
    }

    /**
     * Builds a chained and ordered listener pipeline for the given event type.
     * <p>
     * Listeners are collected by traversing the event class hierarchy and sorting
     * them by {@link EventPriority} according to the internal priority ordering.
     *
     * @param eventType The event class for which to build the listener chain.
     * @return A chained {@link Listener} instance, or {@code null} if no listeners exist.
     * @since 3.8.13
     */
    @SuppressWarnings("unchecked")
    private Listener bake(@NotNull Class<? extends Event> eventType) {
        List<Listener> ordered = new ArrayList<>();

        for (Class<?> current = eventType;
             current != null && Event.class.isAssignableFrom(current);
             current = current.getSuperclass()) {

            Map<EventPriority, List<Listener>> map =
                    registeredListeners.get((Class<? extends Event>) current);

            if (map == null) {
                continue;
            }

            for (EventPriority priority : REVERSED_PRIORITIES) {
                List<Listener> list = map.get(priority);
                if (list != null) {
                    ordered.addAll(list);
                }
            }
        }

        if (ordered.isEmpty()) {
            return null;
        }

        return ordered.stream().reduce((current, next) -> {
            next.setNext(current);
            return next;
        }).orElseThrow();
    }

    /**
     * Checks if the given {@link ListenerAdapter} is registered.
     * This class is a wrapper for {@link ListenerRegistry#isRegistered(Class)}.
     *
     * @param listenerAdapter The {@link ListenerAdapter} to check.
     * @return {@code true} when the {@link ListenerAdapter} was registered, {@code false} otherwise.
     */
    public boolean isRegistered(@NotNull ListenerAdapter listenerAdapter) {
        return listenerIndex.contains(listenerAdapter);
    }

    /**
     * Checks if the given class representation of the {@link ListenerAdapter} is registered.
     *
     * @param type The class representation of the {@link ListenerAdapter} to check.
     * @return {@code true} when the {@link ListenerAdapter} was registered, {@code false} otherwise.
     */
    public boolean isRegistered(@NotNull Class<? extends ListenerAdapter> type) {
        for (ListenerAdapter adapter : listenerIndex) {
            if (type.isInstance(adapter)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calls the event by invoking all registered listeners for the given event type
     * in order of their {@link EventPriority}.
     *
     * @param event The event to be dispatched.
     * @since 3.8.13
     */
    public void call(@NotNull Event event) {
        Listener listener = bakedListeners.get(event.getClass());
        if (listener != null) {
            listener.accept(event);
        }
    }

    /**
     * Dispatches the given event asynchronously using the default internal executor service.
     *
     * @param event The event to be dispatched asynchronously
     * @return A {@link CompletableFuture} that completes once all listeners have finished processing the event
     * @since 3.8.13
     */
    public CompletableFuture<Event> callAsync(@NotNull Event event) {
        return this.callAsync(event, this.executorService);
    }

    /**
     * Dispatches an event asynchronously using the provided executor while respecting
     * listener priorities.
     * <p>
     * The execution is split into sequential priority stages (from lowest to highest),
     * while listeners within the same priority are executed concurrently using the
     * supplied {@link Executor}.
     *
     * @param event    The event to dispatch
     * @param executor The executor used for asynchronous listener execution
     * @return A {@link CompletableFuture} completing once all listener stages have finished
     * @since 3.8.13
     */
    public CompletableFuture<Event> callAsync(@NotNull Event event, @NotNull Executor executor) {
        event.markAsync();
        event.ensureAsyncAllowed();

        Listener listener = bakedListeners.get(event.getClass());
        if (listener == null) {
            return CompletableFuture.completedFuture(event);
        }

        EnumMap<EventPriority, List<Listener>> priorities = new EnumMap<>(EventPriority.class);
        for (Listener current = listener; current != null; current = current.getNext()) {
            priorities.computeIfAbsent(current.getPriority(), __ -> new ArrayList<>())
                    .add(current);
        }

        return callAsync(event, executor, priorities.get(EventPriority.LOWEST))
                .thenCompose(v -> callAsync(event, executor, priorities.get(EventPriority.LOW)))
                .thenCompose(v -> callAsync(event, executor, priorities.get(EventPriority.NORMAL)))
                .thenCompose(v -> callAsync(event, executor, priorities.get(EventPriority.HIGH)))
                .thenCompose(v -> callAsync(event, executor, priorities.get(EventPriority.HIGHEST)))
                .thenCompose(v -> callAsync(event, executor, priorities.get(EventPriority.MONITOR)))
                .thenApply(v -> event);
    }

    /**
     * Executes a collection of listeners asynchronously using the provided executor.
     * <p>
     * All listeners in the collection are executed in parallel, and the returned
     * future completes when all listener executions have finished.
     *
     * @param event     The event to pass to each listener
     * @param executor  The executor used for asynchronous execution
     * @param listeners The collection of listeners to execute; may be {@code null}
     * @return A {@link CompletableFuture} that completes when all listeners have finished execution
     * @since 3.8.14
     */
    private CompletableFuture<Void> callAsync(@NotNull Event event, @NotNull Executor executor,
                                              @Nullable Collection<Listener> listeners) {
        if (listeners == null || listeners.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>(listeners.size());
        for (Listener listener : listeners) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> listener.call(event),
                    executor
            );
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * Returns the internal {@link CallQueue} responsible for managing
     * deferred and asynchronous event dispatching.
     *
     * @return The {@link CallQueue} instance associated with this registry.
     * @since 3.8.13
     */
    public @NotNull CallQueue getCallQueue() {
        return callQueue;
    }

    /**
     * Retrieves the event type for which the handler is listening.
     *
     * @param method The method which handles a given event type.
     * @return The type of the event the handler listens for.
     * @since 3.8.7
     */
    private static @NotNull Class<? extends Event> getEventTypeOrThrow(@NotNull Method method) {
        String exception = "The method %s is provided with %s but does not include %s as the first argument!".formatted(
                method.getName(), EventHandler.class.getName(), Event.class.getName()
        );

        if (method.getParameterCount() <= 0) {
            throw new IllegalStateException(exception);
        }

        Class<?> parameter = method.getParameters()[0].getType();
        if (!Event.class.isAssignableFrom(parameter)) {
            throw new IllegalStateException(exception);
        }

        return parameter.asSubclass(Event.class);
    }

    /**
     * Returns the global singleton instance of the {@code ListenerRegistry}.
     *
     * @return The globally shared {@code ListenerRegistry} instance.
     * @since 3.8.13
     */
    public static ListenerRegistry global() {
        return GLOBAL;
    }

}
