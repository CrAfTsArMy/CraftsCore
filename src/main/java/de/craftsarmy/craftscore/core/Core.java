package de.craftsarmy.craftscore.core;

import com.mysql.cj.jdbc.Driver;
import de.craftsarmy.craftscore.api.config.AbstractConfigParser;
import de.craftsarmy.craftscore.api.discord.DiscordRPC;
import de.craftsarmy.craftscore.api.discord.DiscordRPCCache;
import de.craftsarmy.craftscore.api.discord.DiscordRPCParty;
import de.craftsarmy.craftscore.api.moduls.AbstractModulManager;
import de.craftsarmy.craftscore.api.mysql.AbstractMySQL;
import de.craftsarmy.craftscore.api.network.AbstractNetworker;
import de.craftsarmy.craftscore.api.threading.AbstractWorker;
import de.craftsarmy.craftscore.buildin.config.ConfigParser;
import de.craftsarmy.craftscore.buildin.moduls.ModulManager;
import de.craftsarmy.craftscore.buildin.mysql.MySQL;
import de.craftsarmy.craftscore.buildin.network.Networker;
import de.craftsarmy.craftscore.buildin.threading.Worker;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class Core {

    private AbstractConfigParser parser;
    private AbstractModulManager modulManager;
    private AbstractMySQL sql;
    private AbstractWorker worker;
    private AbstractNetworker networker;
    private DiscordRPC discordRPC;
    private DiscordRPCCache discordRPCCache;
    private DiscordRPCParty discordRPCParty;
    private static boolean initialized = false;
    private static boolean debug = false;

    public Core init() {
        if (!initialized) {
            parser = new ConfigParser();
            modulManager = new ModulManager();
            sql = new MySQL();
            try {
                Driver driver = new Driver();
                DriverManager.registerDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            initialized = true;
        }
        return this;
    }

    public AbstractConfigParser getConfigParser() {
        return parser;
    }
    public void setConfigParser(AbstractConfigParser parser) {
        this.parser = parser;
    }
    public AbstractModulManager getModulManager() {
        return modulManager;
    }
    public void setModulManager(AbstractModulManager modulManager) {
        this.modulManager = modulManager;
    }
    public AbstractMySQL getSql() {
        return sql;
    }
    public void setSql(AbstractMySQL sql) {
        this.sql = sql;
    }
    public AbstractWorker getWorker() {
        return worker;
    }
    public void enableWorker() {
        worker = new Worker();
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
    public DiscordRPC getDiscordRPC() {
        return discordRPC;
    }

    public DiscordRPC enableDiscordRPC(String applicationID, String largeImageKey, String largeImageText) {
        if (!initialized)
            init();
        setDiscordRPC(new DiscordRPC(largeImageKey, largeImageText));
        return getDiscordRPC().create(applicationID);
    }

    public Core disableDiscordRPC() {
        getDiscordRPC().destroyParty();
        getDiscordRPC().destroy();
        return this;
    }

    public void setDiscordRPC(DiscordRPC discordRPC) {
        this.discordRPC = discordRPC;
    }
    public DiscordRPCCache getDiscordRPCCache() {
        return discordRPCCache;
    }
    public void setDiscordRPCCache(DiscordRPCCache discordRPCCache) {
        this.discordRPCCache = discordRPCCache;
    }
    public DiscordRPCParty getDiscordRPCParty() {
        return discordRPCParty;
    }
    public void setDiscordRPCParty(DiscordRPCParty discordRPCParty) {
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
