package me.nekocloud.lobby.cosmetics.commands;

import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.lobby.cosmetics.guis.EffectsMainGui;
import org.bukkit.Bukkit;

public class EffectsCommand extends CosmeticsCommand {

    public EffectsCommand() {
        super("effects", "effect", "эффекты");
    }

    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        openGui(EffectsMainGui.class, Bukkit.getPlayer(gamerEntity.getName()));
    }
}

