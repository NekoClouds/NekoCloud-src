package me.nekocloud.survival.commons.commands.warp;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.gui.WarpGui;
import me.nekocloud.survival.commons.util.TeleportingUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class WarpCommand extends CommonsCommand {

    private final WarpManager warpManager = CommonsSurvivalAPI.getWarpManager();

    public WarpCommand(ConfigData configData) {
        super(configData, true, "warp", "warps");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (strings.length == 0) {
            WarpGui warpGui = GUI_MANAGER.getGui(WarpGui.class, player);
            if (warpGui != null)
                warpGui.open();
            return;
        }

        String name = strings[0];
        Warp warp = warpManager.getWarp(name);
        if (warp == null) {
            gamerEntity.sendMessageLocale("WARP_NOT_FOUND", name);
            return;
        }

        TeleportingUtil.teleport(player, this, ()-> warp.teleport(player));
    }
}
