package de.craftsarmy.craftscore.minecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    private static CommandMap commandMap;
    private static ConcurrentHashMap<String, ? super AbstractCommand> commands;

    static {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void register(AbstractCommand command) {
        if (commandMap.getCommand(command.getName()) == null) {
            commandMap.register(command.getName(), command);
            commands.put(command.getName(), command);
        }
    }

    public static void unregister(AbstractCommand command) {
        if (commandMap.getCommand(command.getName()) != null) {
            commandMap.getCommand(command.getName()).unregister(commandMap);
            commands.remove(command.getName());
        }
    }

    public static boolean isRegistered(AbstractCommand command) {
        return !commands.isEmpty() && commands.containsKey(command.getName());
    }

}
