package me.nekocloud.packetlib.libraries;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.CoreAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bukkit.BukkitConnector;
import me.nekocloud.core.io.info.filter.ServerFilter;
import me.nekocloud.core.io.packet.bukkit.BukkitPlayerDispatchCommand;
import me.nekocloud.core.io.packet.bukkit.BukkitPlayerRedirect;
import org.bukkit.entity.Player;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CoreAPIImpl implements CoreAPI {
    BukkitConnector connector = (BukkitConnector) CoreConnector.getInstance();
    GamerManager gamerManager = NekoCloud.getGamerManager();

    @Override
    public String getServerName() {
        return connector.getServerName();
    }

    @Override
    public void sendToServer(final Player player, final String regex) {
        val gamer = gamerManager.getGamer(player.getName());
        if (gamer == null)
            return;

        sendToServer(gamer, regex);
    }

    @Override
    public void sendToServer(final BukkitGamer gamer, final String regex) {
        if (Cooldown.hasOrAddCooldown(gamer, "redirect_players", 20))
            return;

        connector.sendPacket(new BukkitPlayerRedirect(
                gamer.getPlayerID(),
                regex,
                ServerFilter.REGEX
        ));
    }

    @Override
    public void executeCommand(final BukkitGamer gamer, final String command) {
        connector.sendPacket(new BukkitPlayerDispatchCommand(
                gamer.getPlayerID(),
                command
        ));
    }

    @Override
    public void sendToHub(Player player) {
        sendToServer(player, "^(hub-[1-9])+$");
    }

    @Override
    public void addOnlineTask(final String regex) {
        connector.addOnlineTask(regex);
    }

    @Override
    public int getOnline(final String regex) {
        return connector.getOnline(regex);
    }

    @Override
    public int getGlobalOnline() {
        return connector.getOnline();
    }
}
