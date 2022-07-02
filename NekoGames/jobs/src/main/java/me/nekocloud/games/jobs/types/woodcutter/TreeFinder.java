package me.nekocloud.games.jobs.types.woodcutter;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TreeFinder { // ПИЗДЕЦ ГОВНОКОДИЩЕ
   final List<Block> blocks = new ArrayList<>();
   final int treeType;
   int woodBlocks;
   int leavesBlocks;

   public TreeFinder(Block block) {
      treeType = block.getData();
      finder(block);
   }

   public boolean isWood() {
      return woodBlocks > 0;
   }

   public boolean isLeaves() {
      return leavesBlocks > 0;
   }

   public boolean checkTreeType(int type) {
      return switch (treeType) {
         case 0, 4, 8 -> type == 0 || type == 4 || type == 8;
         case 1, 5, 9 -> type == 1 || type == 5 || type == 9;
         case 2, 6, 10 -> type == 2 || type == 6 || type == 10;
         case 3, 7, 11 -> type == 3 || type == 7 || type == 11;
         default -> false;
      };
   }

   private void finder(Block block) {
      if (!blocks.contains(block) && this.checkTreeType(block.getData())) {
         blocks.add(block);
         BlockFace[] faces = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

         if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
            ++leavesBlocks;
         }

         if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            ++woodBlocks;
            faces = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST};
         }

         BlockFace[] var3 = faces;
         int var4 = faces.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            BlockFace bf = var3[var5];
            Block rel = block.getRelative(bf);
            if (rel.getType() == Material.LEAVES || rel.getType() == Material.LEAVES_2 || rel.getType() == Material.LOG || rel.getType() == Material.LOG_2) {
               this.finder(rel);
            }
         }

      }
   }
}
