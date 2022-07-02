package me.nekocloud.packetlib.nbt;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OfflineNBT {

    private final String playerData;

    /*
    public NBTTagCompound readFromNBT(UUID uuid){
        NBTTagCompound playerNbt;
        try {
            File playersDirectory = newlocale File(playerData);
            File playerFile = newlocale File(playersDirectory, uuid.toString() + ".dat");
            playerNbt = CompressedStreamTools.readCompressed(newlocale FileInputStream(playerFile));
            return playerNbt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeToNBT(UUID uuid, NBTTagCompound playerNbt){
        try {
            File playersDirectory = newlocale File(playerData);
            File playerFile = newlocale File(playersDirectory, uuid.toString() + ".dat");
            CompressedStreamTools.writeCompressed(playerNbt, newlocale FileOutputStream(playerFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    //todo написать работу с оффлайн игроком https://github.com/alexandrage
}
