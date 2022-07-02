package pw.novit.nekocloud.bungee.gamer;

import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

public class BungeeEntityManager {

    static @Getter final BungeeProxyServer bungee = new BungeeProxyServer();

    public static @Nullable BungeeEntity getEntity(@NotNull CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return BungeeGamer.getGamer((ProxiedPlayer) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            return getBungee();
        } else {
            return null;
        }
    }

}
