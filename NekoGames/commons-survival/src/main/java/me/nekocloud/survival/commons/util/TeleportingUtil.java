package me.nekocloud.survival.commons.util;

import lombok.val;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.listener.PlayerListener;
import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class TeleportingUtil {

    private static final UserManager USER_MANAGER       = CommonsSurvivalAPI.getUserManager();
    private static final GamerManager GAMER_MANAGER     = NekoCloud.getGamerManager();
    private static final Random RANDOM                  = new Random();

    public static void teleport(Player sender, CommonsCommand commonsCommand, Runnable runnable) {
        if (CommonsSurvival.getInstance() == null || !CommonsSurvival.getInstance().isEnabled())
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(sender);
        User user = USER_MANAGER.getUser(sender);
        if (gamer == null || user == null)
            return;

        String name = sender.getName();
        Map<String, Boolean> map = PlayerListener.getMapTpErrorBeforeTp();
        Language lang = gamer.getLanguage();
        int waitTeleport = CommonsSurvival.getConfigData().getInt("waitTeleport");

        if (waitTeleport <= 0
                || gamer.getGroup().getLevel() >= CommonsSurvival.getConfigData().getInt("ignoreWaitTeleport")) {
            runnable.run();
            return;
        }

        if (map.get(name) != null && map.get(name)){
            gamer.sendMessageLocale("TELEPORTING_RUNNING");
            return;
        } else {
            commonsCommand.sendMessageLocale(gamer, "TELEPORTING",
                    String.valueOf(waitTeleport),
                    CommonWords.SECONDS_1.convert(waitTeleport, lang));
        }
        map.put(sender.getName(), true);

        new BukkitRunnable(){
            int time = waitTeleport * 20;
            @Override
            public void run() {
                time--;
                if (map.get(name) != null && map.get(name)){
                    if (time == 0) {
                        BukkitUtil.runTask(runnable);
                        map.put(name, false);
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(CommonsSurvival.getInstance(), 0L, 1L);
    }

    public static void teleportRandom(Player player, World world, int rtpSize) {
      val spawn = getRandomLocation(world, rtpSize);
      player.setFallDistance(0.0f);
      player.teleport(spawn);
   }

   public static boolean teleportPvp(Player player, World world, int rtpSize) {
      val spawn = getPvpLocation(player, world, rtpSize, 0);
      if (spawn == null)
         return false;

      player.setFallDistance(0.0f);
      player.teleport(spawn);
      return true;
   }

   private static int getRandomInRange(int min, int max) {
      return RANDOM.nextInt(max - min) + min;
   }

   public static Location getRandomLocation(World world, int rtpSize) {
      val x = getRandomInRange(-rtpSize, rtpSize);
      val z = getRandomInRange(-rtpSize, rtpSize);
      val y = world.getHighestBlockYAt(x, z);

      val spawn = new Location(world, x, y + 1, z);
      val lavaBlock = spawn.getBlock().getLocation().clone().add(0.0, -1.0, 0.0).getBlock();

      if ((lavaBlock != null && lavaBlock.getType() == Material.LAVA) ||
              spawn.getBlock().getBiome().name().contains("OCEAN") ||
              spawn.getBlock().getBiome().name().contains("RIVER")) {
         return getRandomLocation(world, rtpSize);
      }

      return spawn;
   }

   private static Location getPvpLocation(Player player, World world, int rtpSize, int attempts) {
      if (attempts == 10)
         return null;

      val players = Bukkit.getOnlinePlayers().parallelStream()
              .filter(pl -> pl.getWorld().getName().equals(world.getName()) &&
                      Math.abs(pl.getLocation().getBlockX()) <= rtpSize &&
                      Math.abs(pl.getLocation().getBlockZ()) <= rtpSize).collect(Collectors.toList());

      val pvpPlayer = players.get(RANDOM.nextInt(players.size()));
      if (pvpPlayer.equals(player))
         return getPvpLocation(player, world, rtpSize, attempts + 1);


      val spawn = pvpPlayer.getLocation().clone();
      spawn.add(RANDOM.nextInt(100) - 50, 0.0, RANDOM.nextInt(100) - 50);
      spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));

      val lavaBlock = spawn.getBlock().getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
      if ((lavaBlock != null && lavaBlock.getType() == Material.LAVA) ||
              spawn.getBlock().getBiome().name().contains("OCEAN") ||
              spawn.getBlock().getBiome().name().contains("RIVER")) {
         return getPvpLocation(player, world, rtpSize, attempts + 1);
      }

      return spawn;
   }
}
