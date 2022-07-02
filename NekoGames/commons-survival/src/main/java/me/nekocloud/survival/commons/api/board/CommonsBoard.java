package me.nekocloud.survival.commons.api.board;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.scoreboard.Board;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class CommonsBoard {
   protected static ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();
   protected static GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

   BukkitGamer gamer;
   Board board;

   public CommonsBoard(BukkitGamer owner) {
      this.gamer = owner;
      this.board = SCORE_BOARD_API.createBoard();
      this.board.setDisplayName(owner.getLanguage().getMessage("SURVIVAL_BOARD_NAME"));
   }

   public void show() {
      board.showTo(gamer);
   }

   public void remove() {
      board.remove();
   }
}
