package me.nekocloud.core.discord.listeners;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.common.events.player.PlayerAuthSendCodeEvent;
import me.nekocloud.core.discord.bot.DiscordBot;
import me.nekocloud.core.discord.user.DiscordUser;

import static me.nekocloud.core.common.events.player.PlayerAuthSendCodeEvent.CodeType;

public class AuthDiscordCodeListener implements EventListener {

    private final DiscordBot discordBot = DiscordBot.getInstance();

    @EventHandler
    public void onSendCode(PlayerAuthSendCodeEvent event) {
        if (!(event.getCodeType() == CodeType.DISCORD)) return;

        val authPlayer = event.getAuthPlayer();
        val player = authPlayer.getHandle();

        val discordId = authPlayer.getDiscordId();
        val discordUser = DiscordUser.getDiscordUser(discordId);

        val user = DiscordBot.getInstance()
                .getJda()
                .retrieveUserById(discordId)
                .complete();

        if (user == null || user.isBot() || !user.hasPrivateChannel())
            return;

        val channel = user.openPrivateChannel().complete();

        NekoCore.getInstance().getSchedulerManager().runAsync(() ->
                channel.sendMessage("test").queue(message -> {

        message.addReaction(":white_check_mark:").queue();
        message.addReaction(":x:").queue();
    }));

        player.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fВам было отправлено сообщение в Discord " + authPlayer.getDiscordTag() + "\n" +
                "§d§lАВТОРИЗАЦИЯ §8| §fПодтвердите вход в аккаунт через §9ВКонтакте§f, нажав на кнопку \"§aРазрешить вход\"");
    }

}
