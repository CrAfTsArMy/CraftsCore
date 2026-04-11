package de.craftsblock.craftscore.event.queue;

import de.craftsblock.craftscore.event.Event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Represents a queued event dispatch operation within the event system.
 * <p>
 * A {@code QueuedCall} encapsulates an {@link Event} together with its associated
 * {@link CompletableFuture}, execution mode, and optional asynchronous executor.
 * It is used by the event queue to defer and manage event execution in a controlled
 * and optionally asynchronous manner.
 *
 * @param event         The event instance that is scheduled for execution
 * @param future        The future representing the result of the event execution
 * @param async         Whether the event should be executed asynchronously
 * @param asyncExecutor The executor used for asynchronous execution, if applicable
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see CallQueue
 * @since 3.8.13
 */
record QueuedCall(Event event, CompletableFuture<Event> future,
                  boolean async, Executor asyncExecutor) {

    /**
     * Determines whether this queued call has a dedicated asynchronous executor
     * and is marked for asynchronous execution.
     *
     * @return {@code true} if the call is asynchronous and an executor is provided,
     * otherwise {@code false}
     */
    public boolean hasAsyncExecutor() {
        return async && asyncExecutor != null;
    }

}
