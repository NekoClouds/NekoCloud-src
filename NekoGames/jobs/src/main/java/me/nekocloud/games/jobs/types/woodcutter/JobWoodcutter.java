package me.nekocloud.games.jobs.types.woodcutter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.util.RandomUtil;
import me.nekocloud.games.jobs.Jobs;
import me.nekocloud.games.jobs.api.types.JobType;
import me.nekocloud.games.jobs.board.WoodcutterBoard;
import me.nekocloud.games.jobs.config.JobsConfig;
import me.nekocloud.games.jobs.types.Job;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobWoodcutter extends Job {

   RegionForest region;
   HumanNPC woodcutter;

   public JobWoodcutter(JobsConfig jobsConfig) {
      this.region = jobsConfig.getWoodcutterRegion();
      this.woodcutter = jobsConfig.getWoodcutter();
   }

   public JobType getType() {
      return JobType.WOODCUTTER;
   }

   public Vector animation1() {
      return new Vector(
              RandomUtil.getDouble(-0.1D, 0.1D),
              RandomUtil.getDouble(0.35D, 0.75D),
              RandomUtil.getDouble(-0.1D, 0.1D));
   }

   public void hardBack(TreeMask tree) {

      for (TreeMask.TreeBlock block : tree.getBlocks()) {
         block.getLocation().getBlock().setTypeIdAndData(block.getId(), block.getData(), false);
      }

   }

   public void back(TreeMask tree) {
      List<FallingBlock> blocks = new ArrayList<>();

      for (TreeMask.TreeBlock block : tree.getBlocks()) {
         Location loc = block.getLocation();
         val fb = loc.getWorld().spawnFallingBlock(
                 new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY() + 3.1D, loc.getZ() + 0.5D),
                 block.getId(),
                 block.getData());

         fb.setDropItem(false);
         fb.setHurtEntities(false);
         fb.setCustomNameVisible(false);
         fb.setCustomName("#tree2");

         blocks.add(fb);
      }

      Bukkit.getScheduler().scheduleSyncDelayedTask(Jobs.getInstance(), () -> {

         for (val block : blocks) {
            if (block != null) {
               block.remove();
            }
         }

         this.hardBack(tree);
      }, 13L);
   }

   public void fall(RegionForest forest, Tree tree) {
      TreeMask mask = new TreeMask(tree);

      for (Block block : tree.getBlockList()) {
         Location loc = block.getLocation();
         val fb = block.getWorld().spawnFallingBlock(
                 new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY(), loc.getZ() + 0.5D),
                 block.getType(),
                 block.getData());

         fb.setDropItem(false);
         fb.setHurtEntities(false);
         fb.setVelocity(this.animation1());
         fb.setCustomNameVisible(false);
         fb.setCustomName("#tree");

         block.setType(Material.AIR);
      }

      forest.getMasks().put(mask, System.currentTimeMillis());
   }

   public void onTick() {
      region.getMasks().entrySet().removeIf((mask) -> {
         if (mask.getValue() + ((long) this.region.getCooldown() * 1000 * mask.getKey().getWoodBlocks()) >= System.currentTimeMillis()) {
            return false;
         } else {
            back(mask.getKey());
            return true;
         }
      });
   }

   public void onNpcInteract(GamerInteractNPCEvent e) {
      if (e.getNpc() == woodcutter && e.getAction() == GamerInteractNPCEvent.Action.RIGHT_CLICK) {
         val gamer = e.getGamer();
         if (gamer == null)
            return;

         val marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(gamer.getPlayerID());
         val jobsPlayer = JOBS_MANAGER.getOrLoad(gamer);

         if (jobsPlayer.getWoodsBlocks() == 0) {
            gamer.sendMessageLocale("JOBS_WOODCUTTER_NO_WOODS");
         } else {
            marketPlayer.changeMoney(+jobsPlayer.getWoodsEarned());
            gamer.sendMessageLocale("JOBS_WOODCUTTER_CANCEL", jobsPlayer.getWoodsEarned());
            jobsPlayer.resetWoodsBlocks();
         }
      }
   }

   public void onBlockBreak(BlockBreakEvent event) {
      if (region.isInRegion(event.getBlock().getLocation())) {
         val player = event.getPlayer();
         event.setCancelled(true);

         val gamer = GAMER_MANAGER.getGamer(player.getName());
         if (gamer == null)
            return;

         if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            gamer.sendMessageLocale("JOBS_GAMEMODE");
         } else {
            val finder = new TreeFinder(event.getBlock());
            val jobsPlayer = JOBS_MANAGER.getOrLoad(gamer);

            if (!finder.isLeaves() || !finder.isWood() ||
                    event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2) {
               gamer.sendMessageLocale("JOBS_WOODCUTTER_NOT_WOOD");
            } else {
               val code = event.getBlock().getLocation().getBlockX()
                       * 10000 + event.getBlock().getLocation().getBlockZ() * 100000;

               val tree = region.getTrees().computeIfAbsent(code,
                       (x) -> new Tree(finder.getBlocks(),
                               region.getHealth() * finder.getWoodBlocks()));

               tree.cut();

               int reward;
               if (tree.getHealth() > 0) {
                  reward = (int)(1.0D * jobsPlayer.getMultiple() * jobsPlayer.getCombo());
                  gamer.sendMessageLocale("JOBS_WOODCUTTER_BREAK_WOOD", tree.getHealth(), reward);

                  jobsPlayer.addWoodsBlocks();
                  jobsPlayer.setWoodsEarned(jobsPlayer.getWoodsEarned() + reward);
               } else {
                  fall(region, tree);

                  region.getTrees().entrySet().removeIf((x) -> x.getValue() == tree);

                  reward = (int)(randomizeValue(region.getReward() * finder.getWoodBlocks(), 0.2D, 0.2D) *
                          jobsPlayer.getMultiple() * jobsPlayer.getCombo());

                  jobsPlayer.setWoodsEarned(jobsPlayer.getWoodsEarned() + reward);
                  gamer.sendMessageLocale("JOBS_WOODCUTTER_BREAK_TREE", reward);
               }
            }
         }
      }
   }

   public boolean isInRegion(BukkitGamer gamer) {
      val player = gamer.getPlayer();
      return region.isInRegion(player.getLocation());
   }

   public void setBoard(BukkitGamer gamer) {
      val woodcutterBoard = new WoodcutterBoard(gamer, gamer.getLanguage());
      val survivalBoard = BOARD_MANAGER.getBoard(gamer);
      if (survivalBoard != null) survivalBoard.remove();

      BOARD_MANAGER.setBoard(gamer, woodcutterBoard);
      woodcutterBoard.show();
   }

   public void onDisable() {
      region.getMasks().entrySet().removeIf((mask) -> {
         hardBack(mask.getKey());
         return true;
      });
   }

   public void onEntityChangeBlock(EntityChangeBlockEvent event) {
      if (event.getEntity() != null && event.getEntity().getCustomName() != null) {
         if (event.getEntity().getCustomName().equals("#tree") ||
                 event.getEntity().getCustomName().equals("#tree2")) {
            event.getEntity().remove();

            if (event.getBlock() != null) event.getBlock().setType(Material.AIR);

            event.setCancelled(true);
         }

      }
   }

   private static double randomizeValue(double value, double min, double max) {
      return RandomUtil.getDouble(value - value * min, value + value * max);
   }
}
