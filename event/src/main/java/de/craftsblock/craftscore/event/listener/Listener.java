package de.craftsblock.craftscore.event.listener;

import de.craftsblock.craftscore.event.Cancellable;
import de.craftsblock.craftscore.event.Event;
import de.craftsblock.craftscore.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents a single event handler node in the event dispatch chain.
 * <p>
 * A {@code Listener} is both a {@link Consumer} of {@link Event} instances
 * and a link in a chained execution pipeline. Each listener can delegate
 * execution to the next listener in the chain, allowing ordered and
 * prioritized event processing.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see DirectListener
 * @see ReflectionListener
 * @since 3.8.13
 */
public sealed interface Listener extends Consumer<Event>
        permits DirectListener, ReflectionListener {

    /**
     * Accepts an event and forwards execution into the listener chain.
     *
     * @param event The event being processed.
     */
    @Override
    default void accept(Event event) {
        call(event);
        callNext(event);
    }

    /**
     * Performs the logic of this listener with a given event.
     *
     * @param event The event to perform on.
     * @since 3.8.14
     */
    void call(Event event);

    /**
     * Passes the event to the next listener in the chain, if present.
     * <p>
     * If the event is {@link Cancellable} and has been cancelled, listeners
     * marked to ignore cancelled events will be skipped.
     *
     * @param event The event to forward through the chain.
     */
    default void callNext(Event event) {
        Listener next = getNext();
        if (next == null) {
            return;
        }

        if (event instanceof Cancellable cancellable && cancellable.isCancelled() && next.isIgnoreWhenCancelled()) {
            next.callNext(event);
            return;
        }

        next.accept(event);
    }

    /**
     * Returns the event type this listener is associated with.
     *
     * @return The class of the event handled by this listener.
     */
    Class<? extends Event> getEventType();

    /**
     * Returns the next listener in the execution chain.
     *
     * @return The next {@link Listener}, or {@code null} if none exists.
     */
    @Nullable Listener getNext();

    /**
     * Sets the next listener in the execution chain.
     *
     * @param next The next listener to be executed after this one.
     */
    void setNext(@Nullable Listener next);

    /**
     * Returns the execution priority of this listener.
     *
     * @return The {@link EventPriority} of this listener.
     */
    @NotNull EventPriority getPriority();

    /**
     * Indicates whether this listener should ignore events that have been cancelled.
     *
     * @return {@code true} if cancelled events should be ignored, otherwise {@code false}.
     */
    boolean isIgnoreWhenCancelled();

}
