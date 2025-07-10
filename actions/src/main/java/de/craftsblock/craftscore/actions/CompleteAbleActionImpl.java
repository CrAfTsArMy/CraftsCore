package de.craftsblock.craftscore.actions;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This class is an implementation of the {@link CompleteAbleAction} interface.
 * It allows submitting an action for asynchronous execution and provides methods to handle its completion.
 *
 * @param <T> the type of the action's result
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.3.0
 * @see CompleteAbleAction
 * @since 3.6#15-SNAPSHOT
 */
public class CompleteAbleActionImpl<T> implements CompleteAbleAction<T> {

    private static ExecutorService executor = Executors.newCachedThreadPool();

    static {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    executor.shutdownNow();
            } catch (InterruptedException ignored) {
                executor.shutdownNow();
            }
        }));
    }

    /**
     * Sets the {@link ExecutorService} used for asynchronous execution of actions.
     * <p>
     * The previously used executor will be shut down immediately via {@link ExecutorService#shutdownNow()},
     * and any leftover tasks that were pending on the old executor will be resubmitted
     * to the new executor to ensure no tasks are lost.
     *
     * @param executor the new {@link ExecutorService} to use for execution
     * @since 3.8.10
     */
    public static void setExecutor(ExecutorService executor) {
        Collection<Runnable> leftoverTasks = CompleteAbleActionImpl.executor.shutdownNow();

        for (Runnable runnable : leftoverTasks)
            executor.submit(runnable);

        CompleteAbleActionImpl.executor = executor;
    }

    /**
     * Returns the current {@link ExecutorService} used for asynchronous execution.
     *
     * @return the current {@link ExecutorService}
     * @since 3.8.10
     */
    public static ExecutorService getExecutor() {
        return executor;
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
    public CompletableFuture<T> submit(final Consumer<T> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                T result = action.handle();
                if (consumer != null) consumer.accept(result);

                return result;
            } catch (Exception e) {
                throw new RuntimeException("Could not complete action!", e);
            } finally {
                Thread.currentThread().interrupt();
            }
        }, executor);
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
    public void queue(final Consumer<T> consumer) {
        submit(consumer);
    }
}
