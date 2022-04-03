package de.craftsarmy.craftscore;

import de.craftsarmy.craftscore.api.config.AbstractConfigParser;
import de.craftsarmy.craftscore.api.discord.AbstractDiscordRPC;
import de.craftsarmy.craftscore.api.discord.AbstractDiscordRPCCache;
import de.craftsarmy.craftscore.api.discord.AbstractDiscordRPCParty;
import de.craftsarmy.craftscore.api.moduls.AbstractModulManager;
import de.craftsarmy.craftscore.api.network.AbstractNetworker;
import de.craftsarmy.craftscore.api.threading.AbstractWorker;
import de.craftsarmy.craftscore.buildin.config.ConfigParser;
import de.craftsarmy.craftscore.buildin.moduls.ModulManager;
import de.craftsarmy.craftscore.buildin.network.Networker;
import de.craftsarmy.craftscore.buildin.threading.Worker;

public final class Core {

    private AbstractConfigParser parser;
    private AbstractModulManager modulManager;
    private AbstractWorker worker;
    private AbstractNetworker networker;

    private AbstractDiscordRPC discordRPC;
    private AbstractDiscordRPCCache discordRPCCache;
    private AbstractDiscordRPCParty discordRPCParty;

    private static boolean initialized = false;
    private static boolean debug = false;

    public Core init() {
        if (!initialized) {
            parser = new ConfigParser();
            modulManager = new ModulManager();
            worker = new Worker();
            initialized = true;
        }
        return this;
    }

    public AbstractConfigParser getParser() {
        return parser;
    }

    public void setParser(AbstractConfigParser parser) {
        this.parser = parser;
    }

    public AbstractModulManager getModulManager() {
        return modulManager;
    }

    public void setModulManager(AbstractModulManager modulManager) {
        this.modulManager = modulManager;
    }

    public AbstractWorker getWorker() {
        return worker;
    }

    public void setWorker(AbstractWorker worker) {
        this.worker = worker;
    }

    public AbstractNetworker getNetworker() {
        return networker;
    }

    public void enableNetworking(String endpoint) {
        this.networker = new Networker(endpoint);
    }

    public void setNetworker(AbstractNetworker networker) {
        this.networker = networker;
    }

    public AbstractDiscordRPC getDiscordRPC() {
        return discordRPC;
    }

    public AbstractDiscordRPC enableDiscordRPC(String applicationID, String largeImageKey, String largeImageText) {
        if (!initialized)
            init();
        setDiscordRPC(new AbstractDiscordRPC(largeImageKey, largeImageText) {
        });
        return getDiscordRPC().create(applicationID);
    }

    public Core disableDiscordRPC() {
        getDiscordRPC().destroyParty();
        getDiscordRPC().destroy();
        return this;
    }

    public void setDiscordRPC(AbstractDiscordRPC discordRPC) {
        this.discordRPC = discordRPC;
    }

    public AbstractDiscordRPCCache getDiscordRPCCache() {
        return discordRPCCache;
    }

    public void setDiscordRPCCache(AbstractDiscordRPCCache discordRPCCache) {
        this.discordRPCCache = discordRPCCache;
    }

    public AbstractDiscordRPCParty getDiscordRPCParty() {
        return discordRPCParty;
    }

    public void setDiscordRPCParty(AbstractDiscordRPCParty discordRPCParty) {
        this.discordRPCParty = discordRPCParty;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setDebug(boolean debug) {
        Core.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    private static Core instance;

    public static Core instance() {
        if (instance == null)
            instance = new Core().init();
        return instance;
    }

}
