package me.nekocloud.games.jobs.types.woodcutter;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.nekoapi.utils.region.Region;
import org.bukkit.Location;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegionForest extends Region {
   int reward;
   int health;
   int cooldown;
   Map<Integer, Tree> trees = new HashMap<>();
   Map<TreeMask, Long> masks = new HashMap<>();

   public RegionForest(String world, Location min, Location max,
                       int reward, int health, int cooldown) {
      super(world, min, max);

      this.reward = reward;
      this.health = health;
      this.cooldown = cooldown;
   }
}
