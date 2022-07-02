package me.nekocloud.limbo;

import lombok.Getter;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.game.SubType;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bukkit.BukkitConnector;
import me.nekocloud.core.io.info.filter.ServerFilter;
import me.nekocloud.core.io.packet.bukkit.BukkitPlayerRedirect;
import me.nekocloud.limbo.listeners.LobbyGuardListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Getter
public final class NekoLimbo extends JavaPlugin {

    private Thread restart;
    private Location spawnLocation;

    private final BukkitConnector connector = (BukkitConnector) CoreConnector.getInstance();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerType();

        spawnLocation = stringToLocation(getConfig().getString("spawn"));

        new LobbyGuardListener(this);
        runStopServer();
    }

    private Location stringToLocation(String loc) {
        String[] locSplit = loc.split(";");
        Location location = new Location(Bukkit.getWorld(locSplit[0]),
                Double.parseDouble(locSplit[1]),
                Double.parseDouble(locSplit[2]),
                Double.parseDouble(locSplit[3]));
        if (locSplit.length == 6) {
            location.setPitch(Float.parseFloat(locSplit[4]));
            location.setYaw(Float.parseFloat(locSplit[5]));
        }
        return location;
    }

    private void runStopServer() {
        restart = new Thread() {
            private final String time = getConfig().getString("timerestart");

            @Override
            public void run() {
                try {
                    while (true) {
                        if ((time + ":00").contains(getCurrentTimeStamp()))
                            System.exit(0);

                        Thread.sleep(1000L);
                    }
                }
                catch (InterruptedException ignored) {}
            }
        };
        restart.start();
    }

    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    @Override
    public void onDisable() {
        if (restart == null)
            return;

        restart.interrupt();
    }
    
    private void registerType() {
        String serverType = connector.getServerName().split("-")[0];

        try {
            SubType.current = SubType.valueOf(serverType.toUpperCase());
            GameType.current = SubType.current.getGameType();
        } catch (IllegalArgumentException exception) {
            SubType.current = SubType.getByName(System.getProperty("subType", "misc"));
            GameType.current = Arrays.stream(GameType.values())
                    .filter(gameType -> gameType.getLobbyChannel().equalsIgnoreCase(serverType))
                    .findFirst()
                    .orElse(SubType.current.getGameType());
        }

        Bukkit.getLogger().info("Тип сервера определен как §d" + GameType.current.name());
        Bukkit.getLogger().info("Подтип сервера определен как §d" + SubType.current.name());
    }

    public void sendToHub(Player player) {
        if (Cooldown.hasCooldown(player.getName(), "redirect_players"))
            return;

        Cooldown.addCooldown(player.getName(), "redirect_players", 10L);

        connector.sendPacket(new BukkitPlayerRedirect(GlobalLoader.containsPlayerID(player.getName()),
                "hub", ServerFilter.MIN_ONLINE));
    }
}
