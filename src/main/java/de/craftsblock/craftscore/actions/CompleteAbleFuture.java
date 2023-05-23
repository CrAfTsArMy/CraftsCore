package de.craftsblock.craftscore.actions;

import java.util.concurrent.CompletableFuture;

public class CompleteAbleFuture<T> extends CompletableFuture<T> {

    private final CompleteAbleAction<T> restAction;

    public CompleteAbleFuture(CompleteAbleAction<T> restAction) {
        this.restAction = restAction;
    }

}
