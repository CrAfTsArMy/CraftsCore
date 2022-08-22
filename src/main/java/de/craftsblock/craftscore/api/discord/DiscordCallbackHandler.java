package de.craftsblock.craftscore.api.discord;

import de.craftsblock.craftscore.api.threading.AbstractWorker;
import de.craftsblock.craftscore.core.Core;

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
