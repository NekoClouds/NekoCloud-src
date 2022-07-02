package pw.novit.nekocloud.bungee.commands;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class NekoBungeeCommand extends ProxyCommand<NekoBungeeAPI> {

    public NekoBungeeCommand(final NekoBungeeAPI nekoBungeeAPI) {
        super(nekoBungeeAPI,"nekobungee", "nekob", "nb", "nbungee", "bungeeapi");
    }

    @Override
    public void execute(
            final BungeeEntity entity,
            final String @NotNull[] args
    ) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!entity.isHuman() || ((BungeeGamer) entity).isDeveloper()) {
                // Загружаем новые кфг
                plugin.getAnnounceManager().getTask().cancel();
                plugin.loadConfigs();
                entity.sendMessage("§d§lNeko§f§lCloud §8| §fКонфиг плагина BungeeAPI успешно перегружен!");
            } else {
                message(entity);
            }
            return;
        }
        message(entity);
    }

    @Override
    public @NotNull Iterable<String> tabComplete(
            final BungeeEntity entity,
            final String @NotNull[] args
    ) {
        if (args.length == 0) return Collections.emptyList();

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            val search = args[0].toLowerCase();
            if (search.startsWith("r")) matches.add("reload");
          return matches;
        }

        return Collections.emptyList();
    }

    private void message(final @NotNull BungeeEntity entity) {
        entity.sendMessage("§d§lNeko§f§lCloud §8| §fNekoCloud BungeeAPI by §b_Novit_ §f| §bgithub.com/novitpw");
    }
}