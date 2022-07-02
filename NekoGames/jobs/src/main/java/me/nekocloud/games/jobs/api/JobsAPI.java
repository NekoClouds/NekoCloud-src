package me.nekocloud.games.jobs.api;

import lombok.experimental.UtilityClass;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.games.jobs.Jobs;
import me.nekocloud.games.jobs.manager.CraftJobsManager;

@UtilityClass
public final class JobsAPI {

   private static final JobsManager JOBS_MANAGER = new CraftJobsManager(Jobs.getInstance());

   public static JobsManager getJobsManager() {
      return JOBS_MANAGER;
   }
}
