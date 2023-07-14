package de.craftsblock.craftscore.actions;

import java.util.concurrent.CompletableFuture;

/**
 * Defines a set of methods for performing an action asynchronously and handling its completion.
 *
 * @author CraftsBlock
 * @version 1.0
 * @see CompleteAbleActionImpl
 * @since 3.6#15-SNAPSHOT
 * @param <T> the type of the action's result
 */
public interface CompleteAbleAction<T> {

    /**
     * Submits the action for execution and returns a {@link CompletableFuture<T>} representing the result of the action.
     *
     * @return a CompletableFuture representing the result of the action
     */
    CompletableFuture<T> submit();

    /**
     * Submits the action for execution and returns a {@link CompletableFuture<T>} representing the result of the action.
     * Additionally, it allows specifying a {@link Consumer<T>} to handle the result once it's available.
     *
     * @param consumer the {@link Consumer<T>} to handle the result of the action
     * @return a {@link CompletableFuture<T>} representing the result of the action
     */
    CompletableFuture<T> submit(Consumer<T> consumer);

    /**
     * Completes the action synchronously and returns its result.
     *
     * @return the result of the action
     */
    T complete();

    /**
     * Adds the action to a queue for later execution.
     * The execution of the action is not guaranteed to be immediate.
     */
    void queue();

    /**
     * Adds the action to a queue for later execution.
     * Additionally, it allows specifying a {@link Consumer<T>} to handle the result once it's available.
     * The execution of the action is not guaranteed to be immediate.
     *
     * @param consumer the {@link Consumer<T>} to handle the result of the action
     */
    void queue(final Consumer<T> consumer);

}
