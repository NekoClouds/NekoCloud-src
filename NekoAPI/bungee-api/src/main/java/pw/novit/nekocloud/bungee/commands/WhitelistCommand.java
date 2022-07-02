package pw.novit.nekocloud.bungee.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;

import java.util.HashSet;
import java.util.Set;

public final class WhitelistCommand extends ProxyCommand<NekoBungeeAPI> {
    
    public WhitelistCommand(final NekoBungeeAPI nekoBungeeAPI) {
        super(nekoBungeeAPI, "whitelist", "wl");
        setGroup(Group.ADMIN);
    }

    @Override
    public void execute(
            final BungeeEntity entity,
            final String @NotNull[] strings
    ) {
        if (strings.length == 0) {
            entity.sendMessage(new TextComponent(
                        """
                            §d     /wl on/off §f- Включить/Выключить белый список
                            §d     /wl list §f- Вывести список игроков в белом списке
                            §d     /wl add/del <игрок> §f- Добавить/удалить игрока из белого списка
                            """));
            return;
        }

        val command = strings[0];
        switch (command.toLowerCase()) {
            case "on" -> {
                plugin.getWhitelistManager().setEnable(true);

                entity.sendMessage("§d§lNeko§f§lCloud §8| §fБелый список §dвключен");
            }
            case "off" -> {
                plugin.getWhitelistManager().setEnable(false);

                entity.sendMessage("§d§lNeko§f§lCloud §8| §fБелый список §5выключен");
            }
            case "add" -> {
                if (strings.length < 2) {
                    entity.sendMessage("§d§lNeko§f§lCloud §8| §fОшибка, используй: §d/wl add <ник>");
                    break;
                }
                plugin.getWhitelistManager().addPlayer(strings[1].toLowerCase());

                entity.sendMessage("§d§lNeko§f§lCloud §8| §fИгрок §d"
                        + strings[1].toLowerCase() + " §fдобавлен в белый список!");

            }
            case "del", "delete" -> {
                if (strings.length < 2) {
                    entity.sendMessage("§d§lNeko§f§lCloud §8| §fОшибка, используй: §d/wl del <ник>");
                    break;
                }
                plugin.getWhitelistManager().removePlayer(strings[1].toLowerCase());
                entity.sendMessage("§d§lNeko§f§lCloud §8| §fИгрок §d"
                        + strings[1].toLowerCase() + " §fудален из белого списка!");
            }
            case "list" -> {
                entity.sendMessage("§d§lNeko§f§lCloud §8| §fИгроки в белом списке:");
                entity.sendMessage(" §5" + Joiner.on("§7, §5").join(
                        plugin.getWhitelistManager().getPlayerNames()));
            }
            default -> entity.sendMessage(new TextComponent(
                    """
                            §d     /wl on/off §f- Включить/Выключить белый список
                            §d     /wl list §f- Вывести список игроков в белом списке
                            §d     /wl add/del <игрок> §f- Добавить/удалить игрока из белого списка"""));
        }
    }

    @Override
    public @NotNull Iterable<String> tabComplete(
            final BungeeEntity entity,
            final String @NotNull[] args
    ) {
        if (args.length == 0) return ImmutableSet.of();

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            matches.add("on");
            matches.add("off");
            matches.add("list");
            matches.add("add");
            matches.add("del");
            return matches;
        }

        return ImmutableSet.of();
    }
}
