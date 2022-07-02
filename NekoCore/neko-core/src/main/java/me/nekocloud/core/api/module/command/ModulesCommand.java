package me.nekocloud.core.api.module.command;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ModulesCommand extends CommandExecutor {


    public ModulesCommand() {
        super("modules", "модули");
    }

    @Override
    protected void execute(CommandSender sender, @NotNull String[] args) {
        val coreModuleCollection= NekoCore.getInstance().getModuleManager().getModuleMap().values();

        val stringBuilder = new StringBuilder();
        for (val coreModule : coreModuleCollection) {
            stringBuilder.append(coreModule.isEnabled() ? ChatColor.GREEN : ChatColor.RED)
                    .append(coreModule.getName()).append("§f, ");
        }

        sender.sendMessage("§dNekoCore §8| §fСписок модулей (" + coreModuleCollection.size() + "): §e" + stringBuilder.substring(0, stringBuilder.toString().length() - 4));

    }
}
