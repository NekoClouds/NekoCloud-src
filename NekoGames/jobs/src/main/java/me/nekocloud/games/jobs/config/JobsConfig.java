package me.nekocloud.games.jobs.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.games.jobs.Jobs;
import me.nekocloud.games.jobs.api.JobsAPI;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.games.jobs.top.JobsTop;
import me.nekocloud.games.jobs.types.miner.JobMiner;
import me.nekocloud.games.jobs.types.woodcutter.JobWoodcutter;
import me.nekocloud.games.jobs.types.woodcutter.RegionForest;
import me.nekocloud.nekoapi.utils.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobsConfig {
   final EntityAPI entityAPI = NekoCloud.getEntityAPI();
   final JobsManager jobsManager = JobsAPI.getJobsManager();

   final FileConfiguration config;

   Region minerRegion;
   RegionForest woodcutterRegion;
   Map<Integer, Integer> minerPrices;

   Location minerLocation;
   Location woodcutterLocation;
   Location minerTop;
   Location woodcutterTop;

   HumanNPC miner;
   HumanNPC woodcutter;

   boolean woodcutterJob = false;
   boolean minerJob = false;

   public JobsConfig(Jobs jobsPlugin) {
      config = jobsPlugin.getConfig();
      load();
   }

   public void load() {
      if (config.contains("miner")) {
         minerJob = true;
         val minerSection = config.getConfigurationSection("miner");
         minerRegion = new Region(minerSection.getString("world"),
                 LocationUtil.stringToLocation(minerSection.getString("min_location"), false),
                 LocationUtil.stringToLocation(minerSection.getString("max_location"), false));

         minerPrices = new HashMap<>();

         minerSection.getConfigurationSection("prices").getValues(true)
                 .forEach((key, value) -> minerPrices.put(Integer.valueOf(key), (Integer) value));

         minerLocation = LocationUtil.stringToLocation(minerSection.getString("npc_location"), true);
         minerTop = LocationUtil.stringToLocation(minerSection.getString("top_location"), false);

         initMiner();
      }

      if (config.contains("woodcutter")) {
         woodcutterJob = true;
         val woodcutterSection = config.getConfigurationSection("woodcutter");
         woodcutterRegion = new RegionForest(woodcutterSection.getString("world"),
                 LocationUtil.stringToLocation(woodcutterSection.getString("min_location"), false),
                 LocationUtil.stringToLocation(woodcutterSection.getString("max_location"), false),

                 woodcutterSection.getInt("wood_reward"),
                 woodcutterSection.getInt("tree_health"),
                 woodcutterSection.getInt("tree_cooldown"));

         woodcutterLocation = LocationUtil.stringToLocation(woodcutterSection.getString("npc_location"), true);
         woodcutterTop = LocationUtil.stringToLocation(woodcutterSection.getString("top_location"), false);

         initWoodCutter();
      }

      initTops();
   }


   void initMiner() {
      miner = entityAPI.createNPC(minerLocation,
              Skin.createSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzNTQ4NjM5Nzc2OCwKICAicHJvZmlsZUlkIiA6ICI0ZTk0ZjQ1Yzc1OWM0MjkzYWYxNmZkZjIxOTIzN2E1ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NDg4NzBjYTc3YWE2OTlkMDE3MDkzYjgyNzc5ZDBhNTk4ZGM0ZjNmNDlkMzUxNTMyYWU0ZDk2MzNkY2MyYTVhIgogICAgfQogIH0KfQ==",
                      "dB9ykIm6sgY3JaGu8EQF008r2hadg/l1BZgmZgdYkwMOBuhNC+UMAJqSXErhImfOfm2YIKz80P+Y+8pjslTL8VgdkyMoL7NTCMfklT1stMWjA2EhhwrfdxbEMa99beowDN+buQWYlyUPwsRMVw9ChfcIs6bb3xhFV4l2UjDHd+9ctDfIw67jMzzRPKlIzYdL6e33YGdewpu5JmG8bVDlqYPcMYPEewoHBLqNJ124bZODGw0CcLTqxds4fx7uiYa5Kc8cUheqqDIoWw2RR1d4XHriWP687hpLOYvsd/1CTyT3gvpwxW1tIq0HxSeWdvLsx4Zczn3ZOKz1LCrGsIDyT+vHwAEdWSdRiht0Ri+heIVgtWr3CoWKFifSgZbktjBSzlWf1qxP3U3+OC6gn9hYcd3JZirOxklffy3GY5AbulsXlxsEEJqR/y1Xcjxmyg+i5aMP6ztQiP7AoNLzm6kIPrbAV12FqIpLCIqvkTKSH+++rhk+He1949m4Ps8FFS1quHUqb3b73mp8fYMNXhLBV6csJ22Euop08SwsDGK9ml5UaHy7AO0LL828qY4MMT8FGfbySt82KW5Op3nX/HCYoJWAjodjhZfaVKYat44m4mIGV4lksEBZZ++rqVgmqiAy8rjT+kypFTeqqVx2LchTkAsylHdBNl966TiIcTni4xo="));
      miner.setPublic(true);
      miner.setHeadLook(true);

      jobsManager.addJob(JobMiner.class, new JobMiner(this));
   }

   void initWoodCutter() {
      woodcutter = entityAPI.createNPC(woodcutterLocation,
              Skin.createSkin("ewogICJ0aW1lc3RhbXAiIDogMTYzNTQ4NjkwODc4MCwKICAicHJvZmlsZUlkIiA6ICI0NTFkZDA3ZTJkMjY0ODA4YTFlYjYxYjYxMTNiMjY5ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYW5seUdyZWciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFjYzQzYzVjYTI4N2ZkYTdhMWU4MGE0NjRjNGNmN2NlMGUwNmYxZDU1MzI4NTY1ZTQyMGQ0MDZmNzg5NDY3YiIKICAgIH0KICB9Cn0=",
                      "vbqQsisySZFqiT28/zfg4+Ro9U10Xl/Nf8cSXMPkO1mlF5THCWJ3I27qcxODyJS9Ydg110XT7hKNvmb3e2uvALQWQ1KK+AbcbXAfQ6hdTnbd7NYP2ScpvLvZI9lafNaHtPkXMdETeRCCl8/9Zyrlr0v56Xu3tpVUG4eIwEyDDpiwKJjS0YM8OR+gH6WrhdSLtJ7ezXbLUkriViJFCHWBm011PrE33TtSuRfizZ8ILF7LeBXHcTyx9W8efXXL86EMbQVH5Qhpce6NPnWRhtHncqflYXVNnR0OOI7opXfuLUMF75QV1IxarSUgHk4GyxHhDVF34mSl+I2llbS0WfRAnTxE84UHer0FE2zwgHTZVPH23o/tFZHje7yvD+YPjzxnMl5ikpX/A4H8qkeJDzUeN4DdfnCtvoGCs06ytKC7e0OEUeHCxu+Zst+CZRaLPeFOaC4px8IZjjM+81S4AGcOfVF1khchOoYfJHcxM3bmSc9slRaSwBk7MQx2Z/7lPusk5dWEAXEZdicSgFddAXW33F/BTfcxl7kulOPYDMjiR2x6jm43prKb+TBQJschMy0s6HgeuiRfkr7nSmwFmSmxNF49S/mgVO8NQ7rJAW+Ln6MzxfHephzbSWjMB7XCo7ntPV+cUnGt2lBqP2BbYzpLaKKNAqpRHIvvEQg+nQ2rm1Q="));
      woodcutter.setPublic(true);
      woodcutter.setHeadLook(true);

      jobsManager.addJob(JobWoodcutter.class, new JobWoodcutter(this));
   }

   public void initTops() {
      val jobsTop = new JobsTop(this);
      Bukkit.getScheduler().runTaskTimerAsynchronously(Jobs.getInstance(), jobsTop, 5L, 18000L);
   }
}
