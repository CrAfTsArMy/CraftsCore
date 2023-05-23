package de.craftsblock.craftscore.actions;

public interface Action<T> {

    T handle() throws Exception;

}
