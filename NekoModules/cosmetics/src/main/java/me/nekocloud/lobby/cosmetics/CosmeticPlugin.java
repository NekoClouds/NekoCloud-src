package me.nekocloud.lobby.cosmetics;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.lobby.cosmetics.commands.CosmeticCommand;
import me.nekocloud.lobby.cosmetics.commands.EffectsCommand;
import me.nekocloud.lobby.cosmetics.guis.effects.ArrowsEffectGui;
import me.nekocloud.lobby.cosmetics.guis.effects.CritsEffectGui;
import me.nekocloud.lobby.cosmetics.guis.EffectsMainGui;
import me.nekocloud.lobby.cosmetics.guis.effects.KillsEffectGui;
import me.nekocloud.lobby.cosmetics.listeners.EffectsListener;
import me.nekocloud.lobby.cosmetics.sql.CosmeticLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class CosmeticPlugin extends JavaPlugin {
    @Getter
    private static CosmeticPlugin instance;

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        registerGuis();

        new EffectsListener(this);
        new EffectsCommand();
        new CosmeticCommand();
    }

    public void onDisable() {
        CosmeticLoader.getDatabase().close();

        unregisterGuis();
    }

    private void registerGuis() {
        val guiManager = NekoCloud.getGuiManager();

        guiManager.createGui(EffectsMainGui.class);
        guiManager.createGui(ArrowsEffectGui.class);
        guiManager.createGui(CritsEffectGui.class);
        guiManager.createGui(KillsEffectGui.class);
    }

    private void unregisterGuis() {
        val guiManager = NekoCloud.getGuiManager();

        guiManager.removeGui(EffectsMainGui.class);
        guiManager.removeGui(ArrowsEffectGui.class);
        guiManager.removeGui(CritsEffectGui.class);
        guiManager.removeGui(KillsEffectGui.class);
    }
}

