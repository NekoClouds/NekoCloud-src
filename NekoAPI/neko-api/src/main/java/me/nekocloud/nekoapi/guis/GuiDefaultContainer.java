package me.nekocloud.nekoapi.guis;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.commands.DonateCommand;
import me.nekocloud.nekoapi.commands.HelpCommand;
import me.nekocloud.nekoapi.guis.basic.*;
import me.nekocloud.nekoapi.guis.basic.donate.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiDefaultContainer {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();
    private final Map<String, Map<Integer, Gui>> guis = new ConcurrentHashMap<>();

    public GuiDefaultContainer() {
        for (val language : Language.values()) {
            addGui(HelpGui.class, language);
            addGui(DonateGui.class, language);
            addGui(RewardHelpGui.class, language);
            addGui(HegentHelpGui.class, language);
            addGui(AkioHelpGui.class, language);
            addGui(TrivalHelpGui.class, language);
            addGui(AxideHelpGui.class, language);
            addGui(NekoHelpGui.class, language);
            addGui(SitesGui.class, language);
        }

        new HelpCommand(this);
        new DonateCommand(this);
    }

    private void addGui(Class<? extends Gui> clazz, Language language) {
        val name = clazz.getSimpleName().toLowerCase();
        try {
            val gui = clazz.getConstructor(GuiDefaultContainer.class, Language.class).newInstance(this, language);
            Map<Integer, Gui> guis = this.guis.computeIfAbsent(name, k -> new HashMap<>());
            guis.put(language.getId(), gui);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openGui(Class<? extends Gui> clazzGui, Player player) {
        val name = clazzGui.getSimpleName().toLowerCase();
        val gamer = gamerManager.getGamer(player);
        if (gamer == null)
            return;

        Map<Integer, Gui> guis = this.guis.get(name);
        if (guis == null)
            return;

        val gui = guis.getOrDefault(gamer.getLanguage().getId(),
                guis.get(Language.DEFAULT.getId()));
        gui.getInventory().openInventory(player);
    }

}
