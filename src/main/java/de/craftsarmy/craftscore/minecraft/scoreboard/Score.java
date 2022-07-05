package de.craftsarmy.craftscore.minecraft.scoreboard;

import de.craftsarmy.craftscore.buildin.animate.Animator;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class Score {

    private String content;
    private Animator<String> animator;

    private final boolean animated;
    private Team animationTarget;
    private StringBuilder identifier = new StringBuilder();

    public Score(String content) {
        this.content = content;
        this.animated = false;
    }

    public Score(Animator<String> stringAnimator) {
        this.animator = stringAnimator;
        this.animated = true;
        String[] tiles = UUID.randomUUID().toString().split("");
        for (String tile : tiles)
            this.identifier.append("§").append(tile);
    }

    public String content() {
        if(animated)
            throw new IllegalStateException("Score is animated!");
        return content;
    }

    public void setTarget(Team animationTarget) {
        if (!animated)
            throw new IllegalStateException("Score is not animate able!");
        this.animationTarget = animationTarget;
    }

    public Team target() {
        if (!animated)
            throw new IllegalStateException("Score is not animate able!");
        return animationTarget;
    }

    public boolean animated() {
        return animated;
    }

    public Animator<String> animator() {
        if (!animated)
            throw new IllegalStateException("Score is not animate able!");
        return animator;
    }

    public String identifier() {
        if (!animated)
            throw new IllegalStateException("Score is not animate able!");
        return identifier.toString();
    }

}
