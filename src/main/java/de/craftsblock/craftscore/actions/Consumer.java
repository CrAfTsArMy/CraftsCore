package de.craftsblock.craftscore.actions;

import java.util.Objects;

/**
 * This interface represents an operation that accepts a single input argument and returns no result.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 3.6#15-SNAPSHOT
 * @param <T> the type of the input to the operation
 */
public interface Consumer<T> {

    /**
     * Performs the operation on the given input argument.
     *
     * @param t the input argument
     * @throws Exception if an error occurs during the operation
     */
    void accept(T t) throws Exception;

    /**
     * Returns a composed Consumer that performs this operation followed by the operation of the specified Consumer.
     *
     * @param after the Consumer to be executed after this operation
     * @return a composed Consumer that performs this operation followed by the operation of the specified Consumer
     * @throws Exception if an error occurs during the operation
     */
    default Consumer<T> andThen(Consumer<? super T> after) throws Exception {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}

