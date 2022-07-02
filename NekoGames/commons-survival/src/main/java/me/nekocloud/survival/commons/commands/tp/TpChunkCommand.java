package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TpChunkCommand extends CommonsCommand {

    public TpChunkCommand(ConfigData configData) {
        super(configData, true, "tpchunk", "chunk");
        setMinimalGroup(configData.getInt("tpPosCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        if (strings.length < 2) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "CHUNK_FORMAT");
            return;
        }

        final int x;
        final int z;

        try {
            x = Integer.parseInt(strings[0]);
            z = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e){
            gamerEntity.sendMessageLocale("TPPOS_ERROR");
            return;
        }

        if (x > 30000000/16 || z > 30000000/16 || x < -30000000/16 || z < -30000000/16) {
            gamerEntity.sendMessageLocale("TPPOS_ERROR_2");
            return;
        }

        World world = player.getWorld();
        Chunk c = world.getChunkAt(x, z);
        Location center = new Location(world, c.getX() << 4, 64, c.getZ() << 4).add(7, 0, 7);

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.CHUNK, center);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        String locString = "§a" + x + "§f, §a" + z;
        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(center)) {
                sendMessageLocale(gamerEntity, "TPPOS", locString);
            }
        });
    }
}
