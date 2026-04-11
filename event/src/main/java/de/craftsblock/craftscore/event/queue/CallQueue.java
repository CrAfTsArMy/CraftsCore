package de.craftsblock.craftscore.event.queue;

import de.craftsblock.craftscore.event.Event;
import de.craftsblock.craftscore.event.ListenerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * A queue-based event dispatch system that allows events to be processed
 * asynchronously or deferred across multiple logical channels.
 * <p>
 * The {@link    CallQueue} acts as an intermediary between event producers and the
 * {@link ListenerRegistry}, enabling controlled execution of events either immediately
 * or at a later dispatch stage. Events can be grouped into separate channels to
 * allow isolated processing flows.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see QueuedCall
 * @since 3.8.13
 */
public class CallQueue {

    private final ListenerRegistry listenerRegistry;
    private final Map<Short, Queue<QueuedCall>> channels = new ConcurrentHashMap<>();

    /**
     * Creates a new {@link CallQueue} bound to the given {@link ListenerRegistry}.
     *
     * @param listenerRegistry The registry used to dispatch queued events.
     */
    public CallQueue(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    /**
     * Queues an event for deferred processing in the default channel (channel 0).
     *
     * @param event The event to be queued.
     */
    public CompletableFuture<Event> queue(@NotNull Event event) {
        return this.queue((short) 0, event);
    }

    /**
     * Queues an event for deferred processing in a specified channel.
     *
     * @param channel The channel ID to queue the event in.
     * @param event   The event to be queued.
     */
    public CompletableFuture<Event> queue(short channel, @NotNull Event event) {
        return this.queue(channel, event, false, null);
    }

    /**
     * Queues an event asynchronously in the default channel (channel 0).
     *
     * @param event The event to be queued.
     * @return A {@link CompletableFuture} that completes once the event is processed.
     */
    public CompletableFuture<Event> queueAsync(@NotNull Event event) {
        return this.queueAsync((short) 0, event);
    }

    /**
     * Queues an event asynchronously in the default channel (channel 0)
     * using the provided executor.
     *
     * @param event    The event to be queued.
     * @param executor The executor used for asynchronous processing.
     * @return A {@link CompletableFuture} that completes once the event is processed.
     */
    public CompletableFuture<Event> queueAsync(@NotNull Event event, @NotNull Executor executor) {
        return this.queueAsync((short) 0, event, executor);
    }

    /**
     * Queues an event asynchronously in a specific channel.
     *
     * @param channel The channel ID to queue the event in.
     * @param event   The event to be queued.
     * @return A {@link CompletableFuture} that completes once the event is processed.
     */
    public CompletableFuture<Event> queueAsync(short channel, @NotNull Event event) {
        return this.queue(channel, event, true, null);
    }

    /**
     * Queues an event asynchronously in a specific channel using the provided executor.
     *
     * @param channel  The channel ID to queue the event in.
     * @param event    The event to be queued.
     * @param executor The executor used for asynchronous processing.
     * @return A {@link CompletableFuture} that completes once the event is processed.
     */
    public CompletableFuture<Event> queueAsync(short channel, @NotNull Event event, @NotNull Executor executor) {
        return this.queue(channel, event, true, executor);
    }

    /**
     * Internal queueing method that stores an event for later dispatch.
     *
     * @param channel  The channel in which the event is stored.
     * @param event    The event to be queued.
     * @param async    Whether the event should be processed asynchronously.
     * @param executor Optional executor for async execution.
     * @return A {@link CompletableFuture} representing the eventual event result.
     */
    private CompletableFuture<Event> queue(short channel, @NotNull Event event, boolean async, @Nullable Executor executor) {
        CompletableFuture<Event> future = new CompletableFuture<>();
        channels.computeIfAbsent(channel, i -> new ConcurrentLinkedQueue<>())
                .add(new QueuedCall(event, future, async, executor));

        return future;
    }

    /**
     * Processes and dispatches all queued events across all channels.
     */
    public void dispatchAll() {
        for (Short channel : channels.keySet()) {
            dispatch(channel);
        }
    }

    /**
     * Processes and dispatches all queued events in the default channel (channel 0).
     */
    public void dispatch() {
        dispatch((short) 0);
    }

    /**
     * Processes and dispatches all queued events in a specific channel.
     *
     * @param channel The channel ID to process queued events from.
     */
    public void dispatch(short channel) {
        Queue<QueuedCall> queue = channels.get(channel);
        if (queue == null) {
            return;
        }


        QueuedCall queuedCall;
        while ((queuedCall = queue.poll()) != null) {
            CompletableFuture<Event> future = queuedCall.future();

            try {
                Event event = queuedCall.event();
                if (queuedCall.async()) {
                    dispatchAsync(queuedCall, event)
                            .whenComplete((result, throwable) -> {
                                if (throwable != null) {
                                    future.completeExceptionally(throwable);
                                } else {
                                    future.complete(event);
                                }
                            });
                } else {
                    listenerRegistry.call(event);
                    future.complete(event);
                }

            } catch (Exception exception) {
                future.completeExceptionally(exception);
            }
        }

        channels.computeIfPresent(channel, (key, q) -> q.isEmpty() ? null : q);
    }

    /**
     * Dispatches an event asynchronously using either a custom executor or the
     * default asynchronous execution strategy of the {@link ListenerRegistry}.
     *
     * @param call  The queued call containing execution configuration.
     * @param event The event to dispatch.
     * @return A {@link CompletableFuture} representing the async execution result.
     */
    private CompletableFuture<Event> dispatchAsync(@NotNull QueuedCall call, @NotNull Event event) {
        if (call.hasAsyncExecutor()) {
            return listenerRegistry.callAsync(event, call.asyncExecutor());
        }

        return listenerRegistry.callAsync(event);
    }

}
