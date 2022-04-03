package de.craftsarmy.craftscore.api.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import de.craftsarmy.craftscore.Core;
import de.craftsarmy.craftscore.api.threading.AbstractWorker;

public abstract class AbstractDiscordRPC {

    private final DiscordRPC lib = DiscordRPC.INSTANCE;
    private final String largeImageKey$final;
    private final String largeImageText$final;
    private String largeImageKey;
    private String largeImageText;
    private AbstractDiscordRPCCache rpcCache;
    private AbstractDiscordRPCParty rpcParty;
    private boolean party = false;
    private static boolean created = false;
    private long start;
    private Thread worker;

    public AbstractDiscordRPC(String largeImageKey, String largeImageText) {
        this.largeImageKey$final = largeImageKey;
        this.largeImageText$final = largeImageText;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.start = -1;
    }

    public AbstractDiscordRPC create(String applicationId) {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, "");
        Core.instance().getWorker().submit(Startup.class);
        worker.start();
        return this;
    }

    private abstract class Startup extends AbstractWorker.Task {

        public Startup(Class<?> from) {
            super(from);
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public void callback(Class<?> clazz) {
            if (Core.isDebug())
                System.out.println("[RPC Startup Callback]: Startup was successfully completed");
        }

    }

    public void destroy() {
        worker.interrupt();
        worker = null;
        party = false;
        rpcCache = null;
        rpcParty = null;
        start = -1;
        lib.Discord_Shutdown();
    }

    public AbstractDiscordRPC update(String state, String details, String smallKey, String smallText) {
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
        rpcCache = new AbstractDiscordRPCCache(state, details, smallKey, smallText) {
        };
        return this;
    }

    public AbstractDiscordRPC setupParty(AbstractDiscordRPCParty rpcParty) {
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

}
