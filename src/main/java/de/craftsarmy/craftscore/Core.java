package de.craftsarmy.craftscore;

import de.craftsarmy.craftscore.api.config.AbstractConfigManager;
import de.craftsarmy.craftscore.api.config.AbstractConfigParser;
import de.craftsarmy.craftscore.api.moduls.AbstractModulManager;
import de.craftsarmy.craftscore.api.threading.AbstractWorker;
import de.craftsarmy.craftscore.buildin.ConfigParser;

public final class Core {

    private AbstractConfigManager configManager;
    private AbstractConfigParser parser;

    private AbstractModulManager modulManager;

    private AbstractWorker worker;

    private boolean initialized = false;

    public void init() {
        if (!initialized) {
            parser = new ConfigParser();
            initialized = true;
        }
    }

    public AbstractConfigParser getParser() {
        return parser;
    }

    public void setParser(AbstractConfigParser parser) {
        this.parser = parser;
    }

    
}
