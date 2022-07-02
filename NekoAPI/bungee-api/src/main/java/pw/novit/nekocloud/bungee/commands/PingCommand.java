package pw.novit.nekocloud.bungee.commands;

import com.google.common.collect.ImmutableSet;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.util.HashSet;
import java.util.Set;

public final class PingCommand extends ProxyCommand<NekoBungeeAPI> {

    public PingCommand(final NekoBungeeAPI nekoBungeeAPI) {
        super(nekoBungeeAPI, "ping", "пинг");

        setOnlyPlayers(true);
        setCooldown(3, "ping_command");
    }

    @Override
    public void execute(
            final BungeeEntity entity,
            final String @NotNull[] strings
    ) {
        val gamer = (BungeeGamer) entity;

        if (strings.length == 0) {
            gamer.sendMessagesLocale("PING", "§d" + gamer.getPlayer().getPing() + "§f");
            return;
        }

        if (strings.length == 1 && gamer.getGroup().getLevel() >= Group.MODERATOR.getLevel()) {
            val target = BungeeGamer.getGamer(strings[0]);
            if (target == null) {
                playerNeverPlayed(entity, strings[0]);
                return;
            }

            if (!target.isOnline()) {
                playerOffline(entity, strings[0]);
                return;
            }

            val ping = "§d" + plugin.getProxy().getPlayer(strings[0]).getPing() + "§f";
            gamer.sendMessageLocale("PING_OTHER", target.getDisplayName(), ping);
        } else {
            gamer.sendMessageLocale("NO_PERMS");
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
            val search = args[0].toLowerCase();
            for (val player : ProxyServer.getInstance().getPlayers())
                if (player.getName().toLowerCase().startsWith(search))
                    matches.add(player.getName());
          return matches;
        }

        return ImmutableSet.of();
    }

}
