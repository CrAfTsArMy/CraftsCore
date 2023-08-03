package de.craftsblock.craftscore.actions;

import java.util.concurrent.CompletableFuture;

/**
 * This class extends {@link CompletableFuture} and represents a future result of a {@link CompleteAbleAction}.
 *
 * @param <T> the type of the action's result
 * @author CraftsBlock
 * @version 1.0
 * @since 3.6#15-SNAPSHOT
 */
public class CompleteAbleFuture<T> extends CompletableFuture<T> {

    private final CompleteAbleAction<T> restAction;

    /**
     * Constructs a new instance associated with the specified {@link CompleteAbleAction}.
     *
     * @param restAction the {@link CompleteAbleAction} associated with this future
     */
    public CompleteAbleFuture(CompleteAbleAction<T> restAction) {
        this.restAction = restAction;
    }

}

