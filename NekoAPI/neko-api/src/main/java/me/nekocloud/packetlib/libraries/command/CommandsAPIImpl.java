package me.nekocloud.packetlib.libraries.command;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.CommandsAPI;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class CommandsAPIImpl implements CommandsAPI {

    private final CommandManager commandManager = new CommandManager();

    @Override
    public SpigotCommand register(String name, CommandInterface commandInterface, String... aliases) {
        return new CraftSpigotCommand(commandManager, name, commandInterface, aliases);
    }

    @Override
    public List<String> getCompleteString(Collection<String> seeList, String[] args) {
        String lastWord = args[args.length - 1];

        ArrayList<String> matched = new ArrayList<>();
        for (String string : seeList) {
            if (!StringUtil.startsWithIgnoreCase(string, lastWord)) {
                continue;
            }

            matched.add(string);
        }

        matched.sort(String.CASE_INSENSITIVE_ORDER);
        return matched;
    }

    @Override
    public void disableCommand(SpigotCommand command) {
        commandManager.unregister(command);
        disableCommand(command.getName());
    }

    @Override
    public void disableCommand(String name) {
        try {
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);

            Map<String, Command> map = (Map) knownCommands.get(commandManager.getCommandMap());
            Command command = map.remove(name.toLowerCase());
            if (command == null) {
                return;
            }

            command.getAliases().forEach(aliases -> map.remove(aliases.toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disableCommand(Command command) {
        disableCommand(command.getName());
    }

    @Override
    public void disableCommands(Plugin plugin) {
        if (plugin == null) {
            return;
        }

        List<Command> commandList = PluginCommandYamlParser.parse(plugin);
        commandList.forEach(this::disableCommand);
    }

    @Override
    public Command getCommand(String name) {
        try {
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);

            Map<String, Command> map = (Map) knownCommands.get(commandManager.getCommandMap());

            return map.get(name.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<String, SpigotCommand> getCommands() {
        return new HashMap<>(commandManager.getCommands());
    }

    @Override
    public void notEnoughArguments(GamerEntity gamerEntity, String prefix, String key) {
        val lang = gamerEntity.getLanguage();
        gamerEntity.sendMessage(lang.getMessage("ERROR_COMMAND", lang.getMessage(prefix), lang.getMessage(key)));
    }

    @Override
    public void notEnoughArguments(final GamerEntity entity, final String label) {
        val lang = entity.getLanguage();
        entity.sendMessage(lang.getMessage("ERROR_COMMAND", label));
    }

    @Override
    public void playerOffline(GamerEntity gamerEntity, String name) {
        gamerEntity.sendMessageLocale("NO_FOUND_PLAYER", name);
    }

    @Override
    public void playerNeverPlayed(GamerEntity gamerEntity, String name) {
        gamerEntity.sendMessageLocale("NO_NEVER_PLAYER", name);
    }

    @Override
    public void showHelp(GamerEntity gamerEntity, String command, String key) {
        Language lang = gamerEntity.getLanguage();
        gamerEntity.sendMessage(lang.getMessage("HELP_PAPER_FOR", command));
        lang.getList(key).forEach(gamerEntity::sendMessage);
    }
}
