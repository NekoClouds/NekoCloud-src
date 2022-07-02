package me.nekocloud.survival.commons.managers;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.survival.commons.api.board.CommonsBoard;
import me.nekocloud.survival.commons.api.manager.CommonsBoardManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CraftCommonsCommonsBoardManager implements CommonsBoardManager {

   private final Map<String, CommonsBoard> boards = new HashMap<>();

   public Collection<CommonsBoard> getBoards() {
      return boards.values();
   }

   public CommonsBoard getBoard(BukkitGamer gamer) {
      return boards.get(gamer.getName());
   }

   public void setBoard(BukkitGamer gamer, CommonsBoard board) {
      boards.put(gamer.getName(), board);
   }

   public void removeBoard(BukkitGamer gamer) {
      boards.remove(gamer.getName());
   }
}
