package de.craftsblock.craftscore.actions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is an implementation of the {@link CompleteAbleAction} interface.
 * It allows submitting an action for asynchronous execution and provides methods to handle its completion.
 *
 * @param <T> the type of the action's result
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.1
 * @see CompleteAbleAction
 * @see CompleteAbleFuture
 * @since 3.6#15-SNAPSHOT
 */
public class CompleteAbleActionImpl<T> implements CompleteAbleAction<T> {

    private static final ExecutorService service = Executors.newCachedThreadPool();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                service.shutdown();
                if (!service.awaitTermination(1, TimeUnit.SECONDS))
                    service.shutdownNow();
            } catch (InterruptedException ignored) {
                service.shutdownNow();
            }
        }));
    }

    private final Action<T> action;

    /**
     * Constructs a new "CompleteAbleActionImpl" instance with the specified action.
     *
     * @param action the action to be executed
     */
    public CompleteAbleActionImpl(Action<T> action) {
        this.action = action;
    }

    /**
     * Submits the action for execution and returns a {@link CompletableFuture<T>} representing the result of the action.
     *
     * @return a CompletableFuture representing the result of the action
     */
    @Override
    public CompletableFuture<T> submit() {
        return submit(null);
    }

    /**
     * Submits the action for execution and returns a {@link CompletableFuture<T>} representing the result of the action.
     * Additionally, it allows specifying a {@link Consumer<T>} to handle the result once it's available.
     *
     * @param consumer the {@link Consumer<T>} to handle the result of the action
     * @return a {@link CompletableFuture<T>} representing the result of the action
     */
    @Override
    public CompletableFuture<T> submit(Consumer<T> consumer) {
        CompleteAbleFuture<T> restFuture = new CompleteAbleFuture<>(this);
        service.submit(() -> {
            try {
                T result = action.handle();
                restFuture.complete(result);
                if (consumer != null) consumer.accept(result);
                Thread.currentThread().interrupt();
            } catch (InterruptedException ignore) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return restFuture;
    }

    /**
     * Completes the action synchronously and returns its result.
     *
     * @return the result of the action
     */
    @Override
    public T complete() {
        return submit().join();
    }

    /**
     * Adds the action to a queue for later execution.
     * The execution of the action is not guaranteed to be immediate.
     */
    @Override
    public void queue() {
        queue(null);
    }

    /**
     * Adds the action to a queue for later execution.
     * Additionally, it allows specifying a {@link Consumer<T>} to handle the result once it's available.
     * The execution of the action is not guaranteed to be immediate.
     *
     * @param consumer the {@link Consumer<T>} to handle the result of the action
     */
    @Override
    public void queue(Consumer<T> consumer) {
        submit(consumer);
    }
}
