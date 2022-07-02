package me.nekocloud.games.jobs;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.games.jobs.api.JobsAPI;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.games.jobs.config.JobsConfig;
import me.nekocloud.games.jobs.data.JobsStatsLoader;
import me.nekocloud.games.jobs.listeners.JobsListener;
import me.nekocloud.games.jobs.types.Job;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import me.nekocloud.survival.commons.CommonsSurvival;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class Jobs extends JavaPlugin {

   @Getter
   private static Jobs instance;

   private JobsManager manager;
   private MySqlDatabase mySql;
   private FileConfiguration config;

   public void onLoad() {
      instance = this;
   }

   public void onEnable() {
      val file = new File(CoreUtil.getConfigDirectory() + "/jobs.yml");
      if (!file.exists()) {
         Bukkit.getLogger().warning("§c[Jobs] [ERROR] §fКонфиг не найден! Плагин работать не будет!");
         Bukkit.getPluginManager().disablePlugin(this);
         return;
      }

      mySql = MySqlDatabase.newBuilder()
              .data(CommonsSurvival.getConfigData().getDataBase())
              .host(ConnectionConstants.DOMAIN.getValue())
              .password(ConnectionConstants.PASSWORD.getValue())
              .user("neko")
              .create();

      val configManager = new ConfigManager(file);
      config = configManager.getConfig();

      JobsStatsLoader.init(mySql);

      new JobsConfig(this);
      new JobsListener(this);
      manager = JobsAPI.getJobsManager();

      Bukkit.getScheduler().runTaskTimer(this, () ->
              manager.getJobs().forEach(Job::onTick), 0L, 20L);
   }

   public void onDisable() {
      manager.getJobs().forEach(Job::onDisable);
      mySql.close();
   }
}
