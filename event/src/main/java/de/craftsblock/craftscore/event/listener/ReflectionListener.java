package de.craftsblock.craftscore.event.listener;

import de.craftsblock.craftscore.event.Event;
import de.craftsblock.craftscore.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A {@link Listener} implementation that invokes event handler methods via reflection.
 * <p>
 * This listener wraps a reflective method invocation using {@link MethodHandle} for
 * improved performance compared to traditional reflection. It supports both instance
 * and static methods and binds instance methods to their owning listener object.
 * <p>
 * It is primarily used for annotation-driven event handlers (e.g. methods annotated
 * with {@link de.craftsblock.craftscore.event.EventHandler @EventHandler}).
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see Listener
 * @since 3.8.13
 */
public final class ReflectionListener implements Listener {

    private final Class<? extends Event> eventType;
    private final Method method;
    private final MethodHandle handle;

    private final Object owner;
    private final EventPriority priority;
    private final boolean ignoreWhenCancelled;

    private Listener next = null;

    /**
     * Creates a new reflection-based listener for the given event handler method.
     *
     * @param eventType            The class of the event handled by this listener.
     * @param method               The reflective method to invoke when the event is fired.
     * @param owner                The instance owning the method (ignored for static methods).
     * @param priority             The execution priority of this listener.
     * @param ignoreWhenCancelled  Whether this listener should ignore cancelled events.
     */
    public ReflectionListener(
            Class<? extends Event> eventType,
            Method method,
            Object owner,
            EventPriority priority,
            boolean ignoreWhenCancelled
    ) {
        this.eventType = eventType;
        try {
            this.method = method;
            method.setAccessible(true);

            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                    method.getDeclaringClass(),
                    MethodHandles.lookup()
            );

            MethodHandle handle = lookup.unreflect(method);
            if (!Modifier.isStatic(method.getModifiers())) {
                handle = handle.bindTo(owner);
            }

            MethodType targetType = MethodType.methodType(void.class, eventType);
            this.handle = handle.asType(targetType);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create MethodHandle for " + method, e);
        }
        this.owner = owner;
        this.priority = priority;
        this.ignoreWhenCancelled = ignoreWhenCancelled;
    }

    /**
     * {@inheritDoc}
     *
     * @param event {@inheritDoc}
     */
    @Override
    public void accept(Event event) {
        try {
            handle.invokeExact(event);
        } catch (Throwable e) {
            throw new RuntimeException(
                    "Could not invoke listener callback %s".formatted(handle),
                    e
            );
        }

        Listener.super.callNext(event);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Class<? extends Event> getEventType() {
        return eventType;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public @Nullable Listener getNext() {
        return next;
    }

    /**
     * {@inheritDoc}
     *
     * @param next {@inheritDoc}
     */
    @Override
    public void setNext(@Nullable Listener next) {
        this.next = next;
    }

    /**
     * Returns the underlying reflective method associated with this listener.
     *
     * @return The handler {@link Method} instance.
     */
    public @NotNull Method getMethod() {
        return method;
    }

    /**
     * Returns the owning instance of the reflective listener method.
     * <p>
     * For static methods, this value may represent a logical owner or context object
     * depending on how the listener was registered.
     *
     * @return The owner object of this listener.
     */
    public @NotNull Object getOwner() {
        return owner;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public @NotNull EventPriority getPriority() {
        return priority;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isIgnoreWhenCancelled() {
        return ignoreWhenCancelled;
    }

}
