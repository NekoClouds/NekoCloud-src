package me.nekocloud.survival.commons.commands.home;

import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import org.bukkit.entity.Player;

public class SetHomeCommand extends CommonsCommand {

    public SetHomeCommand(ConfigData configData) {
        super(configData,true, "sethome", "createhome");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Language lang = gamerEntity.getLanguage();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SERVER_PREFIX", "SETHOME_FORMAT");
            return;
        }

        int homes = user.getHomes().size();
        Group group = gamer.getGroup();

        Integer limit = configData.getHomeLimit(group);
        if (limit == null)
            limit = configData.getHomeLimit(Group.DEFAULT);

        if (homes == limit) {
            gamer.sendMessageLocale("HOME_WARP_LIMIT", limit,
                    CommonWords.HOMES_1.convert(limit, lang));
            return;
        }

        String nameHome = args[0].toLowerCase();
        if (user.getHomes().containsKey(nameHome)) {
            gamer.sendMessageLocale("HOME_ERROR", nameHome);
            return;
        }

        sendMessageLocale(gamerEntity, "HOME_CREATE", nameHome);
        user.addHome(nameHome, player.getLocation());
    }
}
