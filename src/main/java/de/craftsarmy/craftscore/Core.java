package de.craftsarmy.craftscore;

import de.craftsarmy.craftscore.api.config.AbstractConfigParser;
import de.craftsarmy.craftscore.api.moduls.AbstractModulManager;
import de.craftsarmy.craftscore.api.threading.AbstractWorker;
import de.craftsarmy.craftscore.buildin.ConfigParser;
import de.craftsarmy.craftscore.buildin.ModulManager;
import de.craftsarmy.craftscore.buildin.Worker;

public final class Core {

    private AbstractConfigParser parser;
    private AbstractModulManager modulManager;
    private AbstractWorker worker;

    private static boolean initialized = false;
    private static boolean debug = false;

    public Core init() {
        if (!initialized) {
            parser = new ConfigParser();
            modulManager = new ModulManager();
            worker = new Worker(false);
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
