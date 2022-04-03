package de.craftsarmy.craftscore.api.discord;

import de.craftsarmy.craftscore.Core;

import java.util.UUID;

public abstract class AbstractDiscordRPCParty {

    private final String id;
    private final String joinsecret;
    private int size;
    private int max;


    public AbstractDiscordRPCParty(int max) {
        this.id = UUID.randomUUID().toString();
        this.joinsecret = UUID.randomUUID().toString().substring(0, 28).replace("-", "").toUpperCase();
        this.size = 1;
        this.max = max;
    }

    public void addSize() {
        if (size < max)
            this.size += 1;
    }

    public void removeSize() {
        if (size >= 1)
            this.size -= 1;
        if (size == 0)
            Core.instance().getDiscordRPC().destroyParty();
    }

    public void setSize(int size) {
        if (size <= max)
            this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public String getId() {
        return id;
    }

    public String getJoinsecret() {
        return joinsecret;
    }

}
