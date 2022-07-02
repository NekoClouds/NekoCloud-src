package pw.novit.nekocloud.bungee.api.bossbar;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface BossBarAPI {

    void sendBossBar(ProxiedPlayer player, String text, BossBarStyle style, BossBarColor color, float time);

    void removeBossBar(ProxiedPlayer player);
}

