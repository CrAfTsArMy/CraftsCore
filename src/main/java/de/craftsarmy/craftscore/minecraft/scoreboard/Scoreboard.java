package de.craftsarmy.craftscore.minecraft.scoreboard;

import de.craftsarmy.craftscore.minecraft.Minecraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Scoreboard {

    private final ConcurrentHashMap<Integer, Score> scores = new ConcurrentHashMap<>();
    private final Player owner;

    private org.bukkit.scoreboard.Scoreboard scoreboard;
    private boolean build = false;

    private boolean need$animation = false;
    private int animation$interval = 20;
    private BukkitTask animation;
    private final Runnable animation$runnable;

    public Scoreboard(Player player) {
        this.owner = player;
        animation$runnable = () -> {
            for (Map.Entry<Integer, Score> e : scores.entrySet()) {
                Score obj = e.getValue();
                ScoreChangeEvent event = new ScoreChangeEvent(owner, obj.animator().cycleGet());
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled())
                    obj.target().setPrefix(event.getContent());
            }
        };
    }

    public Score getScore(int score) {
        return scores.get(score);
    }

    public Scoreboard setScore(int score, Score data) {
        scores.put(score, data);
        return this;
    }

    public Scoreboard show() {
        if (!build)
            throw new IllegalStateException("The Scoreboard must be built first by using \"build(String)\"");
        owner.setScoreboard(scoreboard);
        return this;
    }

    public Scoreboard hide() {
        if (!build)
            throw new IllegalStateException("The Scoreboard must be built first by using \"build(String)\"");
        owner.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
        return this;
    }

    public Scoreboard build(String display$name) {
        return build(display$name, DisplaySlot.SIDEBAR);
    }

    public Scoreboard build(String display$name, DisplaySlot slot) {
        org.bukkit.scoreboard.Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective obj = board.registerNewObjective(UUID.randomUUID().toString(), "dummy", display$name);
        obj.setDisplaySlot(slot);

        for (int key : scores.keySet())
            if (scores.get(key).animated()) {
                Team team = board.registerNewTeam(UUID.randomUUID().toString());
                team.addEntry(scores.get(key).identifier());
                obj.getScore(scores.get(key).identifier()).setScore(key);
                scores.get(key).setTarget(team);
                need$animation = true;
            } else
                obj.getScore(scores.get(key).content()).setScore(key);

        if (need$animation)
            animation = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    Minecraft.instance().plugin(),
                    animation$runnable,
                    0,
                    animation$interval
            );

        this.scoreboard = board;
        build = true;
        return this;
    }

    public void setInterval(int animation$interval) {
        if (animation != null) {
            animation.cancel();
            animation = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    Minecraft.instance().plugin(),
                    animation$runnable,
                    0,
                    animation$interval
            );
        } else
            this.animation$interval = animation$interval;
    }

    public Player getOwner() {
        return owner;
    }

    public static Scoreboard create(Player owner) {
        return new Scoreboard(owner);
    }

}
