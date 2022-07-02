package me.nekocloud.core.connector.bungee;

import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.api.utils.ChatUtil;

import java.util.concurrent.TimeUnit;

class ShittyTab {

    ShittyTab(final BungeeConnectorPlugin connectorPlugin,
              final BungeeConnector connector
    ) {

        ProxyServer.getInstance().getScheduler().schedule(connectorPlugin, () -> {
            for (val player : BungeeCord.getInstance().getPlayers()) {
                val gamer = BungeeGamer.getGamer(player);
                if (gamer == null)
                    return;

                val server = player.getServer() == null ? "" : player.getServer().getInfo().getName();

                val globalOnline = connector.getOnline();

                // Так надо :(
                int serverOnline;
                if (player.getServer() == null) serverOnline = 0;
                else serverOnline = player.getServer().getInfo().getPlayers().size();

                val lang = gamer.getLanguage();

                val header = ChatUtil.getComponentFromList(lang.getList(
                        "TAB_HEADER", server,
                        serverOnline, globalOnline, 1000));
                val footer = ChatUtil.getComponentFromList(
                        lang.getList("TAB_FOOTER"));

                player.setTabHeader(header, footer);
            }
        }, 0L, 2L, TimeUnit.SECONDS);
    }
}
