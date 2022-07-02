package me.nekocloud.rofl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PidorListener extends DListener<AntiPidor> {

    Collection<String> pidorList = new ArrayList<>();
    NmsManager nmsManager = NmsAPI.getManager();

    public PidorListener(AntiPidor javaPlugin) {
        super(javaPlugin);

        pidorList.clear();
        pidorList.addAll(javaPlugin.getConfig().getStringList("pidor-list"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();

        if (pidorList.contains(player.getName().toLowerCase())) {
            nmsManager.sendCrashClientPacket(player);
        }
    }
}
