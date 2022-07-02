package me.nekocloud.games.jobs.types.woodcutter;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.block.Block;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class Tree {
   final List<Block> blockList;
   int health;

   public void cut() {
      --health;
   }
}
