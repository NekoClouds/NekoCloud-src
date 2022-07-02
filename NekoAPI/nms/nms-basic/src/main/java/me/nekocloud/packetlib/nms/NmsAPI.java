package me.nekocloud.packetlib.nms;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class NmsAPI {

    @Getter
    private NmsManager manager;

    public void init(final JavaPlugin loader) {
        if (manager != null)
            return;

        try {
            manager = (NmsManager) Class.forName("me.nekocloud.packetlib.nms." + SVersionUtil.SERVER_VERSION +
                    ".NmsManager_" + SVersionUtil.SERVER_VERSION).getConstructor().newInstance();

        } catch (final Exception e) {
            e.printStackTrace();
            loader.getServer().getConsoleSender().sendMessage("§4NMS API not found, Plugin NekoAPI disabled (");
            Bukkit.getPluginManager().disablePlugin(loader);
            Bukkit.setWhitelist(true);
        }
    }
}
