package me.nekocloud.lobby.placeholder.hologram;

import com.google.common.collect.Iterators;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HolderHoloAnimation implements Supplier<String> {

    Iterator<String> colors;
    BukkitGamer gamer;

    public HolderHoloAnimation(@NotNull Player player) {
        GamerManager gamerManager = NekoCloud.getGamerManager();
        gamer = gamerManager.getGamer(player);
        colors = Iterators.cycle(Arrays.asList("d", "6", "e", "a", "b"));
    }

    @Override
    public String get() {
        if (gamer == null) {
            return "";
        }

        return colors.next() + "";
    }
}
