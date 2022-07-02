package me.nekocloud.survival.commons.commands;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.survival.commons.config.ConfigData;

public class AfkCommand extends CommonsCommand {

   private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

   public AfkCommand(ConfigData settingsConfig) {
      super(settingsConfig, true, "afk", "афк");
      setMinimalGroup(settingsConfig.getInt("afkCommand"));

      spigotCommand.setCooldown( 15, "afkCommand");
   }

   @Override
   public void execute(GamerEntity gamerEntity, String command, String[] args) {
      val gamer = (BukkitGamer) gamerEntity;
      if (gamer == null)
         return;

      val user = USER_MANAGER.getUser(gamer.getName());
      if (user != null) {
         if (user.checkAfk()) {
            user.setAfk(false);
            GAMER_MANAGER.getGamers().values().forEach((all) ->
                    all.sendMessageLocale("PLAYER_CANCEL_AFK", gamer.getName()));
         } else {
            user.setAfk(true);
            GAMER_MANAGER.getGamers().values().forEach((all) ->
                    all.sendMessageLocale("PLAYER_AFK", gamer.getName()));
         }
      }
   }
}
