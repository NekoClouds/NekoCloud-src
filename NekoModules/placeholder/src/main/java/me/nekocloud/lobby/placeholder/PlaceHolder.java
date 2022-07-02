package me.nekocloud.lobby.placeholder;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.placeholder.commands.ReloadCommand;
import me.nekocloud.lobby.placeholder.depend.DonateHolo;
import me.nekocloud.lobby.placeholder.depend.GameInfoHolo;
import me.nekocloud.lobby.placeholder.depend.HolderHolo;
import me.nekocloud.lobby.placeholder.hologram.HolderHoloListener;
import me.nekocloud.lobby.placeholder.npc.HolderNPC;
import me.nekocloud.lobby.placeholder.npc.HolderNPCListener;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Log4j2
@Getter
@FieldDefaults(level = PRIVATE)
// #КодНовита
public class PlaceHolder extends JavaPlugin {

    @Override
    public void onEnable() {

    }

    void loadHolo() {

    }

    void loadNpc() {
        for (String npcName : config.getConfigurationSection("NPC").getKeys(false)) {

        }

    }

    public void reloadConfig() {
        onDisable();
        onEnable();

        // Так надо :(
        for (val player : Bukkit.getOnlinePlayers()) {
            val lang = Language.DEFAULT;

            if (isDonateHolo()) getDonateHolo(lang).showTo(player);
            if (isGameInfoHolo()) getGameInfoHolo(lang).showTo(player);

            for (val holo : getHolderHolos()) {
                if (holo.getLang() == lang) holo.showTo(player);
            }

            getNPCs().forEach(holderNPC -> {
                holderNPC.getHologram(lang).showTo(player);
                holderNPC.getNpc().showTo(player);
            });

        }


    }

    @Override
    public void onDisable() {

    }

    public DonateHolo getDonateHolo(@NotNull Language lang) {
        return null;
    }

    public GameInfoHolo getGameInfoHolo(@NotNull Language lang) {
        return null;
    }

    public Collection<HolderNPC> getNPCs() {
        return holderNpcs.values();
    }

    public HolderNPC getNPC(NPC npc) {
        return holderNpcs.get(npc);
    }
}

