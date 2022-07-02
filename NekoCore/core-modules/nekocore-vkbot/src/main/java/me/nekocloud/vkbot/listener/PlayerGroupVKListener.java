package me.nekocloud.vkbot.listener;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerGroupChangeEvent;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;


public class PlayerGroupVKListener implements EventListener {

    private static final String[] ALERT_MESSAGES = new String[]{
            """
            🔥 Новая покупка!
            ⚙ Игрок: %player
            ⭐ Донат: %group

            Сегодня бухаем!
            """
    };

    @EventHandler
    public void onGroupChange(PlayerGroupChangeEvent event) {
        val corePlayer = event.getCorePlayer();
        val currentGroup = event.getCurrentGroup();

        String randomMessage = ChatColor.stripColor(ALERT_MESSAGES[0]
                .replace("%player", corePlayer.getDisplayName())
                .replace("%group", currentGroup.getNameEn()));

        int vkAdminID1 = 475865634;
        int vkAdminID2 = 358163592;
        int vkAdminID3 = 668098172;

        NekoCore.getInstance().getSchedulerManager().runAsync(() -> {
            new Message().peerId(vkAdminID1).body(randomMessage).send(VkBot.INSTANCE);
            new Message().peerId(vkAdminID2).body(randomMessage).send(VkBot.INSTANCE);
            new Message().peerId(vkAdminID3).body(randomMessage).send(VkBot.INSTANCE);
        });

    }
}

