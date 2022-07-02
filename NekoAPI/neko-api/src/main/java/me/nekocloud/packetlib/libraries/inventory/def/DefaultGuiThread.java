package me.nekocloud.packetlib.libraries.inventory.def;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.gui.DefaultGui;
import me.nekocloud.api.manager.GuiManager;
import org.bukkit.Bukkit;

import java.util.Map;

@SuppressWarnings("all")
public class DefaultGuiThread extends Thread {

    private final GuiManager<DefaultGui<?>> manager = NekoCloud.getGuiManager();

    public DefaultGuiThread() {
        super("DefaultGuiThread");
        start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(50);

                for (val name : manager.getPlayerGuis().keySet()) {
                    final Map<String, DefaultGui<?>> guiList = manager.getPlayerGuis().get(name);
                    if (guiList != null) {
                        guiList.values().forEach(alternateGui -> {
                            if (alternateGui != null) {
                                alternateGui.update();
                            }
                        });
                    }
                }
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clear(final Map<String, Long> data) {
        for (val request : data.entrySet()) {
            val name = request.getKey();
            val time = request.getValue();
            if (time + 122 * 1000 < System.currentTimeMillis())
                data.remove(name);

            val player = Bukkit.getPlayerExact(name);
            if (player == null || !player.isOnline())
                data.remove(name);
        }
    }
}
