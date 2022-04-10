package de.craftsarmy.craftscore.api.discord;

import de.craftsarmy.craftscore.Core;
import de.craftsarmy.craftscore.api.threading.AbstractWorker;

public class DiscordCallbackHandler extends AbstractWorker.Task {

    public DiscordCallbackHandler(Class<?> from) {
        super(from);
    }

    @Override
    public void run() {
        if (Core.instance().getDiscordRPC() != null && Core.instance().getDiscordRPC().getLib() != null) {
            Core.instance().getDiscordRPC().getLib().Discord_RunCallbacks();
        }
    }

}
