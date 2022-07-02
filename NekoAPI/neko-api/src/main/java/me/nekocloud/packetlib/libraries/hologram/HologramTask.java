package me.nekocloud.packetlib.libraries.hologram;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.packetlib.libraries.hologram.lines.CraftAnimationLine;
import me.nekocloud.packetlib.libraries.hologram.lines.CraftItemDropLine;
import me.nekocloud.packetlib.libraries.hologram.lines.CraftItemFloatingLine;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class HologramTask implements Runnable {
    HologramManager hologramManager;

    @Override
    public void run() {
        for (val hologram : hologramManager.getHolograms()) {
            for (val craftHoloLine : hologram.getLines()){

                if (craftHoloLine instanceof CraftItemFloatingLine){
                    if (!(((CraftItemFloatingLine) craftHoloLine).isRotate()))
                        continue;
                    ((CraftItemFloatingLine) craftHoloLine).update();
                }

                if (craftHoloLine instanceof CraftItemDropLine line) {
                    if (line.isPickup()) {
                        for (val player : Bukkit.getOnlinePlayers())
                            line.checkPickup(player, hologram);
                    }
                }

                if (craftHoloLine instanceof CraftAnimationLine animationLine) {
                    if (animationLine.update()) {
                        val replaceText = animationLine.getReplaceText();
                        if (replaceText == null)
                            continue;

                        animationLine.setText(replaceText);
                    }
                }
            }
        }
    }
}
