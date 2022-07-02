package me.nekocloud.games.jobs.api.data;

public interface JobsPlayer {
   int getMinesEarned();
   int getWoodsEarned();
   int getMinesBlocks();
   int getWoodsBlocks();

   double getMultiple();

   double getCombo();

   void setMinesEarned(int earned);

   void setWoodsEarned(int earned);

   void addMinesBlocks();

   void addWoodsBlocks();

   void resetMinesBlocks();

   void resetWoodsBlocks();

   void setMultiple(double multiple);
}
