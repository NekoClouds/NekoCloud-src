package me.nekocloud.vkbot.listener;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerAuthSendCodeEvent;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;

import static me.nekocloud.core.api.event.player.PlayerAuthSendCodeEvent.CodeType;

public class AuthVkCodeListener implements EventListener {

    @EventHandler
    public void onSendCode(PlayerAuthSendCodeEvent event) {
        if (!(event.getCodeType() == CodeType.VK)) return;

        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(event.getPlayerID());
        val player = NekoCore.getInstance().getPlayer(event.getPlayerID());

        val playerVkId = authPlayer.getVkId();
        if (playerVkId == -1) {
            authPlayer.complete();
            return;
        }

        NekoCore.getInstance().getSchedulerManager().runAsync( () -> {
            new Message()
                    .peerId(playerVkId)
                    .body("❗ Был совершен вход на аккаунт " + ChatColor.stripColor(player.getDisplayName()) + "\n" +
                            " IP: " + player.getIp().getHostAddress() + "\n Если это не вы, напишите команду !восстановить, для восстановления аккаунта " +
                            "ИЛИ !кик, для кика аккаунта")

                    .keyboard(true, true)
                    .button(KeyboardButtonColor.POSITIVE, 0,
                            new TextButtonAction("/2fa_accept " + authPlayer.getPlayerName(), "Разрешить вход"))
                    .button(KeyboardButtonColor.NEGATIVE, 0,
                            new TextButtonAction("/2fa_deny " + authPlayer.getPlayerName(), "Запретить вход"))
                    .message()

                    .send(VkBot.INSTANCE);
        });

        player.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fВам было отправлено сообщение в VK (id" + playerVkId + ")\n" +
                "§d§lАВТОРИЗАЦИЯ §8| §fПодтвердите вход в аккаунт через §9ВКонтакте§f, нажав на кнопку \"§aРазрешить вход\"");
    }
}
