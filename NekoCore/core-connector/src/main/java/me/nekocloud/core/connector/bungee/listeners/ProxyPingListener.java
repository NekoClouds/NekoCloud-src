package me.nekocloud.core.connector.bungee.listeners;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.core.connector.bungee.BungeeConnector;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.utils.BungeeUtil;
import pw.novit.nekocloud.bungee.api.utils.hex.NHexUtil;
import pw.novit.nekocloud.bungee.listeners.ProxyListener;

import java.util.Arrays;
import java.util.UUID;

/**
 * #КодНовита
 */
public final class ProxyPingListener extends ProxyListener<NekoBungeeAPI> {

	private final BungeeConnector connector;

	public ProxyPingListener(final NekoBungeeAPI plugin,
							 final BungeeConnector connector
    ) {
		super(plugin);
		this.connector = connector;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPing(final ProxyPingEvent event) { // говно
        val response = event.getResponse();
        if (response == null || event instanceof Cancellable && ((Cancellable) event).isCancelled())
            return;

        val connection = event.getConnection();
        val protocol = response.getVersion();
        val players = response.getPlayers();

        // Так надо :(
        event.registerIntent(plugin);
        BungeeUtil.submitAsync(() -> {
            // TODO @xwhilds: мотд получать с кора, емае....
            String motdHex = Joiner.on("\n")
                    .join(plugin.getConfig().getStringList("motd_hex"));
            String motd = Joiner.on("\n")
                    .join(plugin.getConfig().getStringList("motd"));

            if (plugin.getWhitelistManager().isEnable()) {
                val whitelist_hover = plugin.getWhitelistManager().getHoverText();
                motd = plugin.getWhitelistManager().getMotd();
                motdHex = plugin.getWhitelistManager().getMotd();

                players.setSample(Arrays.stream(whitelist_hover.split("\n"))
                        .map(line -> new ServerPing.PlayerInfo(line, UUID.fromString("00000000-0000-0000-0000-000000000000").toString()))
                        .toArray(ServerPing.PlayerInfo[]::new));
            }

            // Версия того кто пинганул
            int ver = connection.getVersion();

            // 719 - 1.15.2
            if (ver >= 47 && ver <= 719) {
                response.setDescriptionComponent(new TextComponent(motd));
            } else {
                response.setDescription(NHexUtil.fromHexString(motdHex));
            }

            // Устанавливаем общий онлайн с коры
            int online = connector.getOnline();

            // Бустер онлайна
            if (plugin.getConfig().getBoolean("booster.enable"))
                players.setOnline(online * plugin.getConfig().getInt("booster.boost"));
            else
                players.setOnline(online);

            // Устанавливаем фейковый максимальный онлайн
            int maxPlayers = plugin.getConfig().getBoolean("justOneMore") ? online + 1 : 1;
            players.setMax(maxPlayers);

            // Устанавливаем максимальную версию протокола
            protocol.setProtocol(Math.max(47, ver));
            response.setVersion(protocol);

            event.completeIntent(plugin);
        });
    }


}
