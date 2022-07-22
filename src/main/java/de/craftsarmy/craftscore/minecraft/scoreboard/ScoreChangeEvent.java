package de.craftsarmy.craftscore.minecraft.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ScoreChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private String content;

    public ScoreChangeEvent(Player player, String content) {
        this.player = player;
        this.content = content;
    }

    public Player getPlayer() {
        return player;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
