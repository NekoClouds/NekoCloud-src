package me.nekocloud.survival.commons.listener;

import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.manager.CommonsBoardManager;
import org.bukkit.event.EventHandler;

import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.EventPriority.LOWEST;

public class BoardListener extends DListener<CommonsSurvival> {

   private final CommonsBoardManager commonsBoardManager = CommonsSurvivalAPI.getBoardManager();

   public BoardListener(CommonsSurvival commonsSurvival) {
      super(commonsSurvival);
   }

   @EventHandler(priority = HIGHEST)
   private void onAsyncJoin(AsyncGamerJoinEvent event) {
      BukkitGamer gamer = event.getGamer();
      if (gamer == null)
         return;

//      MainBoard mainBoard = new MainBoard(gamer.getPlayer());
//      mainBoard.show();
//      this.commonsBoardManager.setBoard(gamer.getPlayer(), mainBoard);
   }

   @EventHandler(priority = LOWEST)
   private void onQuit(AsyncGamerQuitEvent event) {
//      this.commonsBoardManager.getBoard(event.getGamer().getPlayer()).remove();
//      this.commonsBoardManager.removeBoard(event.getGamer().getPlayer());
   }
}
