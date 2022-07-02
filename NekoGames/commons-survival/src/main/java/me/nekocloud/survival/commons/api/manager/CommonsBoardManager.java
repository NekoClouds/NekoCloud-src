package me.nekocloud.survival.commons.api.manager;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.survival.commons.api.board.CommonsBoard;

import java.util.Collection;

public interface CommonsBoardManager {

   Collection<CommonsBoard> getBoards();
   CommonsBoard getBoard(BukkitGamer gamer);

   void setBoard(BukkitGamer gamer, CommonsBoard board);
   void removeBoard(BukkitGamer gamer);
}
