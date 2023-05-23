package de.craftsblock.craftscore.actions;

import java.util.concurrent.CompletableFuture;

public interface CompleteAbleAction<T> {

    CompletableFuture<T> submit();

    CompletableFuture<T> submit(Consumer<T> consumer);

    T complete();

    void queue();

    void queue(final Consumer<T> consumer);

}
