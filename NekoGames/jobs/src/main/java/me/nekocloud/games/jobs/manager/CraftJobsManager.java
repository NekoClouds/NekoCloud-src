package me.nekocloud.games.jobs.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.games.jobs.Jobs;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.games.jobs.api.data.JobsPlayer;
import me.nekocloud.games.jobs.api.types.JobType;
import me.nekocloud.games.jobs.board.MinerBoard;
import me.nekocloud.games.jobs.board.WoodcutterBoard;
import me.nekocloud.games.jobs.data.CraftJobsPlayer;
import me.nekocloud.games.jobs.types.Job;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayerManager;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.manager.CommonsBoardManager;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CraftJobsManager implements JobsManager {

   Map<String, JobsPlayer> jobsPlayers = new ConcurrentHashMap<>();
   Map<Class<? extends Job>, Job> jobs = new HashMap<>();
   CommonsBoardManager commonsBoardManager = CommonsSurvivalAPI.getBoardManager();
   MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

   public CraftJobsManager(Jobs javaPlugin) {
      Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, () ->
              NekoCloud.getGamerManager().getGamers().values().forEach(gamer -> jobs.values().forEach((job) -> {
                 val marketPlayer = marketPlayerManager.getMarketPlayer(gamer.getPlayerID());
                 // MainBoard mainBoard;
                 JobsPlayer jobsPlayer;

                 if (job.getType() == JobType.MINER) {
                    if (job.isInRegion(gamer)) {
                       if (!(commonsBoardManager.getBoard(gamer) instanceof MinerBoard))
                          job.setBoard(gamer);

                    } else if (commonsBoardManager.getBoard(gamer) instanceof MinerBoard) {
                       commonsBoardManager.getBoard(gamer).remove();
//                       mainBoard = new MainBoard(gamer);
//                       mainBoard.show();

                       //commonsBoardManager.setBoard(gamer, mainBoard);

                       jobsPlayer = getOrLoad(gamer);
                       if (jobsPlayer.getMinesEarned() > 0) {
                          marketPlayer.changeMoney(+jobsPlayer.getMinesEarned());
                          gamer.sendMessageLocale("JOBS_MINER_CANCEL", jobsPlayer.getMinesEarned());
                          jobsPlayer.resetMinesBlocks();
                       }
                    }

                 } else if (job.isInRegion(gamer)) {
                    if (!(commonsBoardManager.getBoard(gamer) instanceof WoodcutterBoard))
                       job.setBoard(gamer);

                 } else if (commonsBoardManager.getBoard(gamer) instanceof WoodcutterBoard) {

                    commonsBoardManager.getBoard(gamer).remove();
//                    mainBoard = new MainBoard(gamer);
//                    mainBoard.show();

//                    commonsBoardManager.setBoard(gamer, mainBoard);
                    jobsPlayer = getOrLoad(gamer);
                    if (jobsPlayer.getWoodsEarned() > 0) {
                       marketPlayer.changeMoney(+jobsPlayer.getWoodsEarned());

                       gamer.sendMessageLocale("JOBS_WOODCUTTER_CANCEL", jobsPlayer.getWoodsEarned());
                       jobsPlayer.resetWoodsBlocks();
                    }
                 }

              })), 5L, 20L);
   }

   public Job getJob(Class<? extends Job> jobType) {
      return jobs.get(jobType);
   }

   public Collection<Job> getJobs() {
      return jobs.values();
   }

   public JobsPlayer getOrLoad(BukkitGamer gamer) {
      if (!jobsPlayers.containsKey(gamer.getName())) {
         jobsPlayers.put(gamer.getName(), new CraftJobsPlayer(gamer));
      }

      return jobsPlayers.get(gamer.getName());
   }

   public void addJob(Class<? extends Job> jobType, Job job) {
      jobs.put(jobType, job);
   }

   public void unloadPlayer(BukkitGamer gamer) {
      jobsPlayers.remove(gamer.getName());
   }
}
