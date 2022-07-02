package me.nekocloud.games.jobs.api.data;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.games.jobs.types.Job;

import java.util.Collection;

public interface JobsManager {
   Job getJob(Class<? extends Job> clazz);

   Collection<Job> getJobs();

   JobsPlayer getOrLoad(BukkitGamer gamer);

   void addJob(Class<? extends Job> clazz, Job job);

   void unloadPlayer(BukkitGamer gamer);
}
