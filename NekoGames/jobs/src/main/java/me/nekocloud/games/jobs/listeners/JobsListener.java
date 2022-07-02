package me.nekocloud.games.jobs.listeners;

import me.nekocloud.api.event.gamer.GamerChangeGroupEvent;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.games.jobs.Jobs;
import me.nekocloud.games.jobs.api.JobsAPI;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class JobsListener extends DListener<Jobs> {

   private final JobsManager jobsManager = JobsAPI.getJobsManager();

   public JobsListener(Jobs plugin) {
      super(plugin);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onJoin(AsyncGamerJoinEvent e) {
      jobsManager.getOrLoad(e.getGamer());
   }

   @EventHandler
   public void onQuit(AsyncGamerQuitEvent e) {
      jobsManager.unloadPlayer(e.getGamer());
   }

   @EventHandler
   public void onNpcInteract(GamerInteractNPCEvent e) {
      jobsManager.getJobs().forEach((job) -> job.onNpcInteract(e));
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onBlockBreak(BlockBreakEvent e) {
      jobsManager.getJobs().forEach((job) -> job.onBlockBreak(e));
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onBlockChange(EntityChangeBlockEvent e) {
      jobsManager.getJobs().forEach((job) -> job.onEntityChangeBlock(e));
   }

   @EventHandler
   public void onChangeGroup(GamerChangeGroupEvent event) {
      jobsManager.getOrLoad(event.getGamer()).setMultiple(event.getGamer().getMultiple());
   }
}
