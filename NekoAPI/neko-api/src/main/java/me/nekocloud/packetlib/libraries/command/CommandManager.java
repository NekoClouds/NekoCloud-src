package me.nekocloud.packetlib.libraries.command;

import lombok.Getter;
import me.nekocloud.packetlib.nms.util.ReflectionUtils;
import me.nekocloud.api.command.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CommandManager {

    private final Map<String, SpigotCommand> commands = new ConcurrentHashMap<>();

    private CommandMap commandMap;

    public CommandMap getCommandMap() {
        if (commandMap == null) {
            try {
                Field commandMapField = ReflectionUtils.getCraftBukkitClass("CraftServer")
                        .getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap)commandMapField.get(Bukkit.getServer());
                commandMapField.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return commandMap;
    }

    public void register(SpigotCommand spigotCommand) {
        String name = spigotCommand.getName();

        commands.putIfAbsent(name, spigotCommand);
    }

    void unregister(SpigotCommand spigotCommand) {
        commands.remove(spigotCommand.getName());
    }
}
