package me.nekocloud.core.connector.bukkit;

import lombok.Getter;
import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bukkit.event.BukkitServerInfoSendEvent;
import me.nekocloud.core.io.info.ServerInfo;
import me.nekocloud.core.io.info.fields.ServerField;
import me.nekocloud.core.io.info.types.DefaultServerInfo;
import me.nekocloud.core.io.packet.bukkit.BukkitServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BukkitConnectorPlugin extends JavaPlugin {

    private BukkitConnector connector;

    @Override
    public void onLoad() {
        Language.reloadAll();
    }

    @Override
    public void onEnable() {
        connector = new BukkitConnector(this);

        CoreConnector.setInstance(connector);
        runTaskAsync(connector::start);
        sendInfo();
    }

    @Override
    public void onDisable() {
        connector.shutdown();
    }

    private void sendInfo() {
        val event = new BukkitServerInfoSendEvent();
        callEvent(event);

        ServerInfo serverInfo = event.getServerInfo();
        Map<ServerField, Object> fields = new HashMap<>();
        if (serverInfo == null) {
            serverInfo = new DefaultServerInfo(340);
            fields.put(ServerField.ONLINE, Bukkit.getOnlinePlayers().size());
            fields.put(ServerField.MAP_NAME, Bukkit.getServer().getWorldType());
            fields.put(ServerField.MAX_PLAYERS, Bukkit.getServer().getMaxPlayers());
        }

        connector.sendPacket(new BukkitServerInfo(
                connector.getServerName(), 340, fields
        ));
    }

    public void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public void runTaskAsync(Runnable runnable){
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

}
