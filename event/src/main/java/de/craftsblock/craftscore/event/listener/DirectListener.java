package de.craftsblock.craftscore.event.listener;

import de.craftsblock.craftscore.event.Event;
import de.craftsblock.craftscore.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A {@link Listener} implementation that directly delegates event handling
 * to a provided {@link Consumer}.
 * <p>
 * This listener is primarily used for programmatic event registration where
 * no reflection-based method invocation is required. It acts as a lightweight
 * wrapper around a functional callback.
 *
 * @param <T> The specific type of {@link Event} handled by this listener
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see Listener
 * @since 3.8.13
 */
public final class DirectListener<T extends Event> implements Listener {

    private final Class<? extends Event> eventType;
    private final Consumer<T> consumer;
    private final EventPriority priority;
    private final boolean ignoreWhenCancelled;

    private Listener next = null;

    /**
     * Creates a new direct listener for the given event type.
     *
     * @param eventType           The class of the event this listener handles.
     * @param consumer            The consumer that will process the event.
     * @param priority            The execution priority of this listener.
     * @param ignoreWhenCancelled Whether this listener should ignore cancelled events.
     */
    public DirectListener(
            Class<? extends Event> eventType,
            Consumer<T> consumer,
            EventPriority priority,
            boolean ignoreWhenCancelled
    ) {
        this.eventType = eventType;
        this.consumer = consumer;
        this.priority = priority;
        this.ignoreWhenCancelled = ignoreWhenCancelled;
    }

    /**
     * {@inheritDoc}
     *
     * @param event {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void accept(Event event) {
        this.accept0((T) event);
    }

    /**
     * Internal type-safe event handling method.
     * <p>
     * Executes the underlying {@link Consumer} and then forwards execution
     * to the next listener in the chain.
     *
     * @param event The event instance to handle.
     */
    public void accept0(T event) {
        getConsumer().accept(event);
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
     * Returns the underlying consumer responsible for handling events.
     *
     * @return The event consumer.
     */
    public @NotNull Consumer<T> getConsumer() {
        return consumer;
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
