package me.nekocloud.core.discord.listeners;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.common.events.player.PlayerAuthSetDiscordEvent;
import me.nekocloud.core.discord.bot.DiscordBot;

@Log4j2
public class PlayerListener implements EventListener {

    @EventHandler
    public void onDiscordChange(PlayerAuthSetDiscordEvent event) {
        val authPlayer = event.getAuthPlayer();
        val playerID = authPlayer.getOfflineHandle().getPlayerID();
        val discordId = authPlayer.getDiscordId();

        // Это при отвязке аккаунта, удаляем данные
        if (discordId == -1) {
            deleteData(playerID);
            return;
        }

        val user = DiscordBot.getInstance()
                .getJda()
                .retrieveUserById(discordId)
                .complete();

        String tag = "Ошибка получения данных";
        if (user != null) {
            tag = user.getAsTag();
        }

        insertData(playerID, discordId, tag);

    }


    private void deleteData(int id) {
        CoreSql.getDatabase().execute("DELETE FROM `discord_data` WHERE `id`=?", id);
    }

    private void insertData(int playerID, long discordId, String tag) {
        CoreSql.getDatabase().execute("INSERT INTO `discord_data` (`Id`, `DiscordId`, `DiscordTag`) VALUES (?, ?, ?)",
                playerID, discordId, tag);
    }
}
