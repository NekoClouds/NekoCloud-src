package me.nekocloud.pexer;

import lombok.Getter;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.nekoapi.listeners.JoinListener;
import me.nekocloud.pexer.listeners.LPListener;
import me.nekocloud.pexer.listeners.PEXListener;
import me.nekocloud.pexer.listeners.SPListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class NekoPexer extends JavaPlugin {

    public GamerManager gamerManager;
    public LuckPermsApi luckPermsApi;

    @Override
    public void onEnable() {
        gamerManager = NekoCloud.getGamerManager();

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            luckPermsApi = LuckPerms.getApi();
            new LPListener(this);
            return;
        } else if (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null) {
            new PEXListener(this);
            return;
        } else if (Bukkit.getPluginManager().getPlugin("SimplePerms") != null) {
            new SPListener(this);
            return;
        } else {

            getLogger().log(Level.SEVERE, "ТЫ ЧЕ ЕБАНУТЫЙ!? Поставь плагин на пермы");
            getLogger().log(Level.SEVERE, "ТЫ ЧЕ ЕБАНУТЫЙ!? Поставь плагин на пермы");
            Bukkit.shutdown();
        }

        new JoinListener(this);
    }

}
