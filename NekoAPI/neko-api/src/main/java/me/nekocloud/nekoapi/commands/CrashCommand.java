package me.nekocloud.nekoapi.commands;

import com.google.common.collect.ImmutableList;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class CrashCommand implements CommandInterface, CommandTabComplete {

    private final NmsManager nmsManager = NmsAPI.getManager();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    public CrashCommand() {
        val spigotCommand = COMMANDS_API.register("crash", this);
        spigotCommand.setGroup(Group.ADMIN);
        spigotCommand.setCommandTabComplete(this);
    }

    @Override
    public void execute(
            final GamerEntity gamerEntity,
            final String command,
            final String @NotNull[] args
    ) {
        if (args.length != 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "CRASH_FORMAT");
            return;
        }

        val name = args[0];
        val target = Bukkit.getPlayer(name);
        val targetGamer = gamerManager.getGamer(target);
        if (target == null || !target.isOnline() || targetGamer == null) {
            COMMANDS_API.playerOffline(gamerEntity, name);
            return;
        }

        if (targetGamer.isOwner()) {
            gamerEntity.sendMessage("§d§lNekoCloud §8| §fПососи)))))");
            return;
        }

        if (gamerEntity.getName().equals(name)) {
            gamerEntity.sendMessageLocale("CRASH_ERROR_YOU");
            return;
        }

        gamerEntity.sendMessageLocale("CRASH_PLAYER", target.getDisplayName());
        nmsManager.sendCrashClientPacket(target);

    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String alias, String[] args) {
        if (gamerEntity.isHuman() && ((BukkitGamer)gamerEntity).getGroup() != Group.ADMIN) {
            return ImmutableList.of();
        }

        if (args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
            return COMMANDS_API.getCompleteString(names, args);
        }

        return ImmutableList.of();
    }
}
