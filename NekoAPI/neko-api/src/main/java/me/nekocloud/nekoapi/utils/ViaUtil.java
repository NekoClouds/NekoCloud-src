package me.nekocloud.nekoapi.utils;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.gamer.constans.Version;
import org.bukkit.entity.Player;

@UtilityClass
public class ViaUtil {

    ViaAPI<?> VIA_API = Via.getAPI();

    public Version getVersion(Player player) {
        return Version.getVersion(VIA_API.getPlayerVersion(player.getUniqueId()));
    }
}
