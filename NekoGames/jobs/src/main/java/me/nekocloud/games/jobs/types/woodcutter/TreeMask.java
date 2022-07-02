package me.nekocloud.games.jobs.types.woodcutter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TreeMask {
   final List<TreeBlock> blocks = new ArrayList<>();

   int woodBlocks;
   int leavesBlocks;

   public TreeMask(Tree tree) {
      for(Block block : tree.getBlockList()) {
         blocks.add(new TreeBlock(block));
         if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
            ++leavesBlocks;
         }

         if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            ++woodBlocks;
         }
      }

   }

   @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
   @Getter
   static class TreeBlock {
      byte data;
      int id;
      Location location;

      public TreeBlock(Block block) {
         this.data = block.getData();
         this.id = block.getTypeId();
         this.location = block.getLocation();
      }
   }
}
