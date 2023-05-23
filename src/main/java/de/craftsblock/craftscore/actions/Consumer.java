package de.craftsblock.craftscore.actions;

import java.util.Objects;

public interface Consumer<T> {

    void accept(T t) throws Exception;

    default Consumer<T> andThen(Consumer<? super T> after) throws Exception {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }

}
