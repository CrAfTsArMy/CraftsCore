package de.craftsblock.craftscore.event;

public interface Cancelable {

    void setCancelled(boolean cancelled);

    boolean isCancelled();

}
