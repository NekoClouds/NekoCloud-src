package me.nekocloud.survival.commons.commands.warp;

import lombok.val;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.events.UserCreateWarpEvent;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.managers.CraftWarpManager;
import me.nekocloud.survival.commons.object.CraftWarp;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreateWarpCommand extends CommonsCommand {

    private final CraftWarpManager warpManager = (CraftWarpManager) CommonsSurvivalAPI.getWarpManager();
    private final Map<Group, Integer> limitWars;

    public CreateWarpCommand(ConfigData configData) {
        super(configData, true, "createwarp", "setwarp");
        limitWars = configData.getWarpLimit();
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        val user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null)
            return;

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity,"SERVER_PREFIX",  "CREATE_WARP_FORMAT");
            return;
        }

        val gamer = (BukkitGamer) gamerEntity;
        val lang = gamer.getLanguage();
        val player = gamer.getPlayer();

        val name = strings[0];
        if (warpManager.getWarps().containsKey(name.toLowerCase())) {
            gamer.sendMessageLocale("WARP_CREATE_ERROR", name);
            return;
        }

        val warps = warpManager.size();
        Integer playerLimit = limitWars.get(gamer.getGroup());
        if (playerLimit == null) {
            playerLimit = limitWars.get(Group.DEFAULT);
        }

        if (warps == playerLimit && !gamer.isStaff()) {
            gamer.sendMessageLocale("HOME_WARP_LIMIT",
                    playerLimit,
                    CommonWords.WARPS_1.convert(playerLimit, lang));
            return;
        }

        //todo сделать покупку варпов

        if (name.length() > 32) {
            gamer.sendMessageLocale("WARP_CREATE_NAME_ERROR");
            return;
        }

        val warp = new CraftWarp(name, gamer.getPlayerID(), player.getLocation(), false);

        val event = new UserCreateWarpEvent(user, warp);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        sendMessageLocale(gamerEntity,  "WARP_CREATE", warp.getName());
        warpManager.addToDataBase(warp);
    }
}
