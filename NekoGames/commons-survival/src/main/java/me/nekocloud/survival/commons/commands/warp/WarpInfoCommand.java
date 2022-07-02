package me.nekocloud.survival.commons.commands.warp;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WarpInfoCommand extends CommonsCommand {

    private final WarpManager warpManager = CommonsSurvivalAPI.getWarpManager();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public WarpInfoCommand(ConfigData configData) {
        super(configData, false, "warpinfo");
        setMinimalGroup(Group.MODERATOR);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String cmd, String[] args) {
        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "WARPINFO_FORMAT");
            return;
        }

        String name = args[0];
        if (!warpManager.getWarps().containsKey(name.toLowerCase())) {
            gamerEntity.sendMessageLocale("WARP_NOT_FOUND", name);
            return;
        }

        Warp warp = warpManager.getWarp(name);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(warp.getDate().getTime());
        String date = simpleDateFormat.format(calendar.getTime());

        sendMessageLocale(gamerEntity, "WARPINFO", warp.getName(), warp.getOwner().getDisplayName(), date);
    }
}
