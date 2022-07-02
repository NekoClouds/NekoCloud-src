package me.nekocloud.core.logger;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.sender.ConsoleCommandSender;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

@Log4j2
public class Logger extends SimpleTerminalConsole {

    protected static final NekoCore nekoCore = NekoCore.getInstance();

    public Logger() {
        super.start();
    }

    @Override
    protected boolean isRunning() {
        return nekoCore.isRunning();
    }

    @Override
    protected void runCommand(final String command) {
        try {
            val commandManager = nekoCore.getCommandManager();

            if (!commandManager.dispatchCommand(ConsoleCommandSender.getInstance(), command)) {
                log.info(ChatColor.RED + "Неизвестная команда :(");
            }
        } catch (final Exception ex) {
            log.info(ChatColor.RED + "Что-то обосралось при выполнении команды!");
        }
    }

    @Override
    protected void shutdown() {
        nekoCore.shutdown();
    }
}
