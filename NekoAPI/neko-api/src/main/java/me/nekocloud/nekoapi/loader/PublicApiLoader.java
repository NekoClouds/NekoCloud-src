package me.nekocloud.nekoapi.loader;

import lombok.val;
import me.nekocloud.entity.NekoGamerManager;
import me.nekocloud.packetlib.libraries.*;
import me.nekocloud.packetlib.libraries.command.CommandsAPIImpl;
import me.nekocloud.packetlib.libraries.effect.ParticleAPIImpl;
import me.nekocloud.packetlib.libraries.entity.EntityAPIImpl;
import me.nekocloud.packetlib.libraries.hologram.HologramAPIImpl;
import me.nekocloud.packetlib.libraries.inventory.InventoryAPIImpl;
import me.nekocloud.packetlib.libraries.inventory.def.DefaultGuiManager;
import me.nekocloud.packetlib.libraries.scoreboard.ScoreBoardAPIImpl;
import me.nekocloud.packetlib.libraries.usableItem.UsableAPIImpl;
import org.bukkit.Bukkit;

final class PublicApiLoader {

    static void init(final NekoAPI nekoApi) {
        Object api = null;
        try {
            val apiClass = Class.forName("me.nekocloud.api.NekoCloud");
            val constructor = apiClass.getConstructor();
            api = constructor.newInstance();
        } catch (final Exception e) {
            e.printStackTrace();
            nekoApi.getServer().getConsoleSender().sendMessage("§4API not found, Plugin NekoAPI disabled (");
            Bukkit.getPluginManager().disablePlugin(nekoApi);
            Bukkit.setWhitelist(true);
        }
        if (api == null)
            return;

        try {
            setStaticField(api, "jsonMessageAPI", new JSONMessageAPIImpl());
            setStaticField(api, "gamerManagerAPI", new NekoGamerManager(nekoApi));
            setStaticField(api, "soundAPI", new SoundAPIImpl());
            setStaticField(api, "usableAPI", new UsableAPIImpl(nekoApi));
            setStaticField(api, "entityAPI", new EntityAPIImpl(nekoApi));
            setStaticField(api, "hologramAPI", new HologramAPIImpl(nekoApi));
            setStaticField(api, "scoreBoardAPI", new ScoreBoardAPIImpl(nekoApi));
            setStaticField(api, "commandsAPI", new CommandsAPIImpl());
            setStaticField(api, "inventoryAPI", new InventoryAPIImpl(nekoApi));
            setStaticField(api, "titleAPI", new TitleAPIImpl());
            setStaticField(api, "borderAPI", new BorderAPIImpl());
            setStaticField(api, "actionBarAPI", new ActionBarAPIImpl());
            setStaticField(api, "particleAPI", new ParticleAPIImpl(nekoApi));
            setStaticField(api, "coreAPI", new CoreAPIImpl());
            setStaticField(api, "guiManager", new DefaultGuiManager());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void setStaticField(Object instance, String fieldName, Object value) throws Exception {
        val clazz = instance.getClass();
        val f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(null, value);
        f.setAccessible(false);
    }
}
