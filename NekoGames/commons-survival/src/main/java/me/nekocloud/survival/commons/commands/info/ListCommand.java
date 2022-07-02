package me.nekocloud.survival.commons.commands.info;

import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ListCommand extends CommonsCommand {

    public ListCommand(ConfigData configData) {
        super(configData, false, "list");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        StringBuilder best = new StringBuilder();
        StringBuilder players = new StringBuilder();
        StringBuilder donate = new StringBuilder();
        StringBuilder staff = new StringBuilder();

        int online = Bukkit.getOnlinePlayers().size();
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                continue;
            String displayName = player.getDisplayName();
            if (gamer.isStaff()) {
                staff.append(displayName).append("§f, ");
                continue;
            }
            if (gamer.getGroup() == Group.DEFAULT) {
                players.append(displayName).append("§f, ");
                continue;
            }
            if (gamer.getGroup() == Group.NEKO || gamer.getGroup() == Group.YOUTUBE || gamer.getGroup() == Group.TIKTOK) {
                best.append(displayName).append("§f, ");
                continue;
            }
            donate.append(displayName).append("§f, ");
        }
        Language lang = gamerEntity.getLanguage();
        gamerEntity.sendMessagesLocale("LIST",
                "§a" + online + " §f" + CommonWords.PLAYERS_1.convert(online, lang),
                correct(best, lang),
                correct(players, lang),
                correct(donate, lang),
                correct(staff, lang)
        );

    }

    private String correct(StringBuilder stringBuilder, Language lang) {
        String list = String.valueOf(stringBuilder);
        if (list.length() == 0)
            return lang.getMessage("LIST_EMPTY");

        return list.substring(0, list.length() - 4);
    }
}
