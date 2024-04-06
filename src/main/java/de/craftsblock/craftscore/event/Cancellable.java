package de.craftsblock.craftscore.event;

public interface Cancellable {

    void setCancelled(boolean cancelled);

    boolean isCancelled();

}
