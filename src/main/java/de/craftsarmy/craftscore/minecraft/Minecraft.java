package de.craftsarmy.craftscore.minecraft;

import org.bukkit.plugin.Plugin;

public class Minecraft {

    private Plugin plugin;
    public void init(Plugin plugin) {
        this.plugin = plugin;
    }
    public Plugin plugin() {
        return plugin;
    }
    private static Minecraft minecraft;

    public static Minecraft instance() {
        if (minecraft == null)
            minecraft = new Minecraft();
        return minecraft;
    }

}
