package me.nekocloud.core.api.command;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public final class CommandManager implements ICommandManager {

    @Getter
    private final Map<String, CommandExecutor> commandMap = new HashMap<>();

    @Override
    public boolean dispatchCommand(
            @NotNull CommandSender commandSender,
            @NotNull String command
    ) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        val commandArg = command.trim().split(" ", -1);
        val commandExecutor = getCommand(commandArg[0]);

        if (commandExecutor == null) {
            return false;
        }

        commandExecutor.onExecute(commandSender, Arrays.copyOfRange(commandArg, 1, commandArg.length));
        return true;
    }

    @Override
    public void registerCommand(final @NotNull CommandExecutor commandExecutor) {
        commandMap.put(commandExecutor.getCommand(), commandExecutor);

        for (String commandAlias : commandExecutor.getAliases()) {
            commandMap.put(commandAlias, commandExecutor);
        }
    }

    @Override
    public boolean commandIsExists(final @NotNull String commandName) {
        String[] commandArg = commandName.replaceFirst("/", "").split(" ", -1);

        return getCommand(commandArg[0]) != null;
    }

    @Override
    public CommandExecutor getCommand(final @NotNull String commandName) {
        return commandMap.get(commandName.toLowerCase());
    }

}
