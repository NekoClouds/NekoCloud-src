package me.nekocloud.games.jobs.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.games.jobs.api.data.JobsPlayer;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CraftJobsPlayer implements JobsPlayer {

   final int playerID;
   double multiple;

   int minesEarned;
   int woodsEarned;

   int minesBlocks;
   int woodsBlocks;

   public CraftJobsPlayer(BukkitGamer gamer) {
      playerID = gamer.getPlayerID();
      multiple = gamer.getMultiple();
   }

   public double getCombo() {
      return woodsBlocks <= 1000 && minesBlocks <= 1000 ?
              (woodsBlocks <= 100 && minesBlocks <= 100 ? 1.0D : 1.5D) : 1.7D;
   }

   public void addMinesBlocks() {
      ++minesBlocks;
   }

   public void addWoodsBlocks() {
      ++woodsBlocks;
   }

   public void resetMinesBlocks() {
      JobsStatsLoader.addValue(playerID, minesEarned, true);

      minesBlocks = 0;
      minesEarned = 0;
   }

   public void resetWoodsBlocks() {
      JobsStatsLoader.addValue(playerID, woodsEarned, false);
      woodsBlocks = 0;
      woodsEarned = 0;
   }
}
