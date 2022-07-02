package me.nekocloud.survival.commons.commands.warp;

import com.google.common.collect.ImmutableList;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.events.UserRemoveWarpEvent;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DelWarpCommand extends CommonsCommand implements CommandTabComplete {

    private final WarpManager warpManager = CommonsSurvivalAPI.getWarpManager();

    public DelWarpCommand(ConfigData configData) {
        super(configData, true, "delwarp", "removewarp");
        spigotCommand.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null) {
            return;
        }

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity,"SERVER_PREFIX",  "DEL_WARP_FORMAT");
            return;
        }

        String name = strings[0];
        if (!warpManager.getWarps().containsKey(name.toLowerCase())) {
            gamerEntity.sendMessageLocale( "WARP_NOT_FOUND", name);
            return;
        }

        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Warp warp = warpManager.getWarp(name);
        if (warp.getOwnerID() != gamer.getPlayerID() && !gamer.isStaff()) {
            gamer.sendMessageLocale("WARP_NOT_YOUR", warp.getName());
            return;
        }

        UserRemoveWarpEvent event = new UserRemoveWarpEvent(user, warp);
        BukkitUtil.callEvent(event);

        if (!event.isCancelled()) {
            sendMessageLocale(gamerEntity, "WARP_REMOVED", warp.getName());
            warpManager.removeWarp(warp);
        }
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(warpManager.getWarps(user)
                .stream()
                .map(Warp::getName)
                .collect(Collectors.toList()), strings);
    }
}
