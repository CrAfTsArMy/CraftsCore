package de.craftsblock.craftscore.actions;

/**
 * Defines a generic action that provides a method to perform a task.
 *
 * @param <T> the type of the action's return value
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0
 * @since 3.6#15-SNAPSHOT
 */
public interface Action<T> {

    /**
     * Executes the action and returns the result.
     *
     * @return the result of the action
     * @throws Exception if an error occurs during execution
     */
    T handle() throws Exception;

}