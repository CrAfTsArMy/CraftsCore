package de.craftsblock.craftscore.actions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompleteAbleActionImpl<T> implements CompleteAbleAction<T> {

    private static final ExecutorService service = Executors.newCachedThreadPool();

    private final Action<T> action;

    public CompleteAbleActionImpl(Action<T> action) {
        this.action = action;
    }

    @Override
    public CompletableFuture<T> submit() {
        return submit(null);
    }

    @Override
    public CompletableFuture<T> submit(Consumer<T> consumer) {
        CompleteAbleFuture<T> restFuture = new CompleteAbleFuture<>(this);
        service.submit(() -> {
            try {
                T result = action.handle();
                restFuture.complete(result);
                if (consumer != null)
                    consumer.accept(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return restFuture;
    }

    @Override
    public T complete() {
        return submit().join();
    }

    @Override
    public void queue() {
        queue(null);
    }

    @Override
    public void queue(Consumer<T> consumer) {
        submit(consumer);
    }

}
