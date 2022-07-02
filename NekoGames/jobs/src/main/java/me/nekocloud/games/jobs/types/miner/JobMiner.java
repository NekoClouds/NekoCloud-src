package me.nekocloud.games.jobs.types.miner;

import lombok.val;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.games.jobs.api.types.JobType;
import me.nekocloud.games.jobs.board.MinerBoard;
import me.nekocloud.games.jobs.config.JobsConfig;
import me.nekocloud.games.jobs.types.Job;
import me.nekocloud.nekoapi.utils.region.Region;
import me.nekocloud.survival.commons.api.board.CommonsBoard;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;
import java.util.Map.Entry;

public class JobMiner extends Job {

   private final Map<Integer, Integer> prices;
   private final Map<Location, Integer> respawn = new HashMap<>();
   private final Random random = new Random();
   private final Region region;
   private final HumanNPC miner;

   public JobMiner(JobsConfig jobsConfig) {
      this.prices = jobsConfig.getMinerPrices();
      this.region = jobsConfig.getMinerRegion();
      this.miner = jobsConfig.getMiner();
   }

   public JobType getType() {
      return JobType.MINER;
   }

   public void onBlockBreak(BlockBreakEvent event) {
      if (region.isInRegion(event.getBlock().getLocation())) {
         event.setCancelled(true);
         val block = event.getBlock();
         val player = event.getPlayer();
         val gamer = GAMER_MANAGER.getGamer(player.getName());
         if (gamer == null)
            return;

         if (!this.prices.containsKey(block.getTypeId())) {
            gamer.sendMessageLocale("JOBS_MINER_NOT_ORE");
         } else if (player.getGameMode() != GameMode.SURVIVAL) {
            gamer.sendMessageLocale("JOBS_GAMEMODE");
         } else {
            respawn.put(block.getLocation(), 15);

            val jobsPlayer = JOBS_MANAGER.getOrLoad(gamer);
            int reward = (int)((double)(random.nextInt(prices.get(block.getTypeId())) + 2) *
                    jobsPlayer.getMultiple() * jobsPlayer.getCombo());

            event.getBlock().setType(Material.STONE);

            jobsPlayer.addMinesBlocks();
            jobsPlayer.setMinesEarned(jobsPlayer.getMinesEarned() + reward);

            gamer.sendMessageLocale("JOBS_MINER_EARNED", reward);
         }
      }
   }

   public void onNpcInteract(GamerInteractNPCEvent e) {
      if (e.getNpc() == miner && e.getAction() == GamerInteractNPCEvent.Action.RIGHT_CLICK) {
         val gamer = e.getGamer();
         if (gamer == null)
            return;

         val marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(gamer.getPlayerID());
         val jobsPlayer = JOBS_MANAGER.getOrLoad(gamer);
         if (jobsPlayer.getMinesBlocks() == 0) {
            gamer.sendMessageLocale("JOBS_MINER_NO_ORE");
         } else {
            marketPlayer.changeMoney(+jobsPlayer.getMinesEarned());
            gamer.sendMessageLocale("JOBS_MINER_CANCEL", jobsPlayer.getMinesEarned());
            jobsPlayer.resetMinesBlocks();
         }
      }
   }

   public boolean isInRegion(BukkitGamer gamer) {
      return region.isInRegion(gamer.getPlayer().getLocation());
   }

   public void setBoard(BukkitGamer gamer) {
      MinerBoard minerBoard = new MinerBoard(gamer, gamer.getLanguage());
      CommonsBoard commonsBoard = BOARD_MANAGER.getBoard(gamer);
      if (commonsBoard != null) {
         commonsBoard.remove();
      }

      BOARD_MANAGER.setBoard(gamer, minerBoard);
      minerBoard.show();
   }

   public void onTick() {
      List<Location> toRemove = new ArrayList<>();

      for (Entry<Location, Integer> entry : this.respawn.entrySet()) {
         if (entry.getValue() <= 1) {
            entry.getKey().getBlock().setType(Material.getMaterial((Integer)
                    prices.keySet().toArray()[random.nextInt(prices.size())]));
            toRemove.add(entry.getKey());
         } else {
            respawn.replace(entry.getKey(), entry.getValue() - 1);
         }
      }

      toRemove.forEach(respawn::remove);
      toRemove.clear();
   }

   public void onDisable() {
      respawn.forEach((key, value) -> key.getBlock().setType(Material.getMaterial((Integer)
              prices.keySet().toArray()[random.nextInt(prices.size())])));
   }
}
