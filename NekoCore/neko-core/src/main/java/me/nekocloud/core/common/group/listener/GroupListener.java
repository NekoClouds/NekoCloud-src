package me.nekocloud.core.common.group.listener;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.util.RandomUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.GroupChangeEvent;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class GroupListener implements EventListener {

    private static final String[] ALERT_MESSAGES = new String[]{
            """
                §8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                
                            §5ВНИМАНИЕ!
                  §fИгрок %player §fкупил привилегию %group
                  §bПоздравим §fего с покупкой!
                  §fХочешь так же? §dwww.nekocloud.me
                
                §8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■""",
            """
                §8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                
                            §5ВНИМАНИЕ!
                  §fУ нас новый счастливчик!
                  §fПоздравляем %player §fс покупкой %group
                
                 §fЕсли Вы хотите, чтобы о Вас узнали также, то
                 §fприобретите любую привилегию на нашем сайте!
                
                 §fМагазин возможностей - §dwww.nekocloud.me
                
                §8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■""",
            """
                §8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                \s
                            §5ВНИМАНИЕ!
                  §6§lНОВЫЙ ДОНАТЕР §f- §6§lСПАСИБО ТЕБЕ
                  %player% §fкупил %group
                  §fна сайте §dwww.nekocloud.me
                
                §8■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■""",
    };

    @EventHandler
    public void onGroupChange(@NotNull GroupChangeEvent event) {
        val corePlayer = event.getCorePlayer();

        val currentGroup = event.getCurrentGroup();
        val previousGroup = event.getPreviousGroup();

        if (previousGroup == null)
            return;

        if (currentGroup != Group.ADMIN
                && currentGroup != Group.MODERATOR
                && currentGroup != Group.SRMODERATOR
                && currentGroup != Group.TIKTOK
                && currentGroup != Group.YOUTUBE
                && currentGroup != Group.BUILDER) {
            String randomMessage = ALERT_MESSAGES[RandomUtil.getInt(0, ALERT_MESSAGES.length - 1)]
                    .replace("%player", corePlayer.getDisplayName())
                    .replace("%group", currentGroup.getNameEn());

            for (val onlinePlayer : NekoCore.getInstance().getOnlinePlayers())
                onlinePlayer.sendMessage(randomMessage);
        }
    }

}
