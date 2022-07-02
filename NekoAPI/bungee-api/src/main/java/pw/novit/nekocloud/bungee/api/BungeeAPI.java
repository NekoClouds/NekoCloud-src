package pw.novit.nekocloud.bungee.api;

import lombok.experimental.UtilityClass;
import pw.novit.nekocloud.bungee.api.bossbar.BossBarAPI;
import pw.novit.nekocloud.bungee.api.bossbar.impl.BossBarAPIImpl;

@UtilityClass
public final class BungeeAPI {

    private BossBarAPI bossBarAPI;

    public BossBarAPI getBossBarAPI() {
        if (bossBarAPI == null) {
            bossBarAPI = new BossBarAPIImpl();
        }

        return bossBarAPI;
    }

}

