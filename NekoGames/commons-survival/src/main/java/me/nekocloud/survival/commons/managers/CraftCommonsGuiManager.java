package me.nekocloud.survival.commons.managers;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.api.manager.GuiManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CraftCommonsGuiManager implements GuiManager<CommonsSurvivalGui> {

    private final Map<String, Map<String, CommonsSurvivalGui>> playerGuis = new ConcurrentHashMap<>();
    private final Set<String> guis = new ConcurrentSet<>();

    @Override
    public void createGui(Class<? extends CommonsSurvivalGui> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        if (guis.contains(name))
            return;

        guis.add(name);
    }

    @Override
    public void removeGui(Class<? extends CommonsSurvivalGui> clazz) {
        String nameClazz = clazz.getSimpleName().toLowerCase();
        for (String name : playerGuis.keySet()) {
            Map<String, CommonsSurvivalGui> guis = playerGuis.get(name);
            for (String guiName : guis.keySet())
                if (guiName.equalsIgnoreCase(nameClazz) )
                    guis.remove(guiName);
        }
    }

    @Override
    public <T extends CommonsSurvivalGui> T getGui(Class<T> clazz, Player player) {
        T gui = null;

        String guiName = clazz.getSimpleName().toLowerCase();
        String name = player.getName().toLowerCase();

        if (guis.contains(guiName)) {
            Map<String, CommonsSurvivalGui> guis = playerGuis.get(name);
            if (guis == null) {
                guis = new ConcurrentHashMap<>();
                playerGuis.put(name, guis);
            }
            gui = (T) guis.get(guiName);

            if (gui == null) {
                try {
                    gui = clazz.getConstructor(Player.class).newInstance(player);
                    guis.put(guiName, gui);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return gui;
    }

    @Override
    public void removeALL(Player player) {
        String name = player.getName().toLowerCase();
        playerGuis.remove(name);
    }


}
