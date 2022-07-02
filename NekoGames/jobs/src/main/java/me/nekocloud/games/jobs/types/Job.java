package me.nekocloud.games.jobs.types;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayerManager;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.manager.CommonsBoardManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import me.nekocloud.games.jobs.api.JobsAPI;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.games.jobs.api.types.JobType;

public abstract class Job {
   protected static final JobsManager JOBS_MANAGER = JobsAPI.getJobsManager();
   protected static final MarketPlayerManager MARKET_PLAYER_MANAGER = MarketAPI.getMarketPlayerManager();
   protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
   protected static final CommonsBoardManager BOARD_MANAGER = CommonsSurvivalAPI.getBoardManager();

   public abstract JobType getType();

   public void onBlockBreak(BlockBreakEvent event) {
   }

   public void onEntityChangeBlock(EntityChangeBlockEvent event) {
   }

   public void onNpcInteract(GamerInteractNPCEvent event) {
   }

   public abstract boolean isInRegion(BukkitGamer gamer);

   public abstract void setBoard(BukkitGamer gamer);

   public void onTick() {
   }

   public void onDisable() {
   }
}
