package de.craftsarmy.craftscore.minecraft.commands;

import org.bukkit.command.defaults.BukkitCommand;

import java.util.List;

public abstract class AbstractCommand extends BukkitCommand {

    public AbstractCommand(String name) {
        this(name, null);
    }

    public AbstractCommand(String name, String description) {
        this(name, description, null);
    }

    public AbstractCommand(String name, String description, String usage) {
        this(name, description, usage, null);
    }

    public AbstractCommand(String name, String description, String usage, List<String> aliases) {
        super(name);
        if (description != null)
            this.description = description;
        if (usage != null)
            this.usageMessage = usage;
        if (aliases != null)
            this.setAliases(aliases);
        CommandManager.register(this);
    }

}
