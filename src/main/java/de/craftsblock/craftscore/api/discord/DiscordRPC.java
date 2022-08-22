package de.craftsblock.craftscore.api.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import de.craftsblock.craftscore.core.Core;

public class DiscordRPC {

    private final club.minnced.discord.rpc.DiscordRPC lib = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
    private final String largeImageKey$final;
    private final String largeImageText$final;
    private String largeImageKey;
    private String largeImageText;
    private DiscordRPCCache rpcCache;
    private DiscordRPCParty rpcParty;
    private boolean party = false;
    private static boolean created = false;
    private long start;

    public DiscordRPC(String largeImageKey, String largeImageText) {
        this.largeImageKey$final = largeImageKey;
        this.largeImageText$final = largeImageText;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.start = -1;
    }

    public DiscordRPC create(String applicationId) {
        if(!created) {
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            handlers.ready = (user) -> System.out.println("Ready! Welcome -> " + user.username + "#" + user.discriminator);
            lib.Discord_Initialize(applicationId, handlers, true, "");
            Core.instance().getWorker().repeat(DiscordCallbackHandler.class);
            created = true;
        }
        return this;
    }

    public void destroy() {
        Core.instance().getWorker().pause(DiscordCallbackHandler.class);
        party = false;
        rpcCache = null;
        rpcParty = null;
        start = -1;
        lib.Discord_Shutdown();
    }

    public DiscordRPC update(String state, String details, String smallKey, String smallText) {
        DiscordRichPresence presence = new DiscordRichPresence();
        if (start == -1)
            start = System.currentTimeMillis() / 1000;
        presence.startTimestamp = start;
        presence.state = state;
        presence.details = details;
        presence.largeImageKey = largeImageKey;
        presence.largeImageText = largeImageText;
        presence.smallImageKey = smallKey;
        presence.smallImageText = smallText;
        if (party) {
            presence.partyId = rpcParty.getId();
            presence.partySize = rpcParty.getSize();
            presence.partyMax = rpcParty.getMax();
            presence.joinSecret = rpcParty.getJoinsecret();
        }
        lib.Discord_UpdatePresence(presence);
        rpcCache = new DiscordRPCCache(state, details, smallKey, smallText) {
        };
        return this;
    }

    public DiscordRPC setupParty(DiscordRPCParty rpcParty) {
        this.rpcParty = rpcParty;
        Core.instance().setDiscordRPCParty(rpcParty);
        party = true;
        update(rpcCache.getState(), rpcCache.getDetails(), rpcCache.getSmallKey(), rpcCache.getSmallText());
        return this;
    }

    public void destroyParty() {
        this.rpcParty = null;
        Core.instance().setDiscordRPCParty(null);
        party = false;
        update(rpcCache.getState(), rpcCache.getDetails(), rpcCache.getSmallKey(), rpcCache.getSmallText());
    }

    public void setLargeImage(String key) {
        this.largeImageKey = key;
    }

    public void resetLargeImage() {
        this.largeImageKey = this.largeImageKey$final;
    }

    public void setLargeImageText(String text) {
        this.largeImageText = text;
    }

    public void resetLargeImageText() {
        this.largeImageText = this.largeImageText$final;
    }

    public DiscordRPCParty getRpcParty() {
        return rpcParty;
    }

    public static boolean isCreated() {
        return created;
    }

    public boolean isParty() {
        return party;
    }

    public club.minnced.discord.rpc.DiscordRPC getLib() {
        return lib;
    }

}
