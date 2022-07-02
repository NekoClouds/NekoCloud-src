package me.nekocloud.nekoapi.utils;

import lombok.experimental.UtilityClass;
import me.nekocloud.base.gamer.constans.Version;
import org.bukkit.entity.Player;
//import protocolsupport.api.ProtocolSupportAPI;

@UtilityClass
public class ProtocolSupportUtil {

    public Version getVersion(Player player) {
        return Version.getVersion(ViaUtil.getVersion(player).getProtocol());
    }
}
