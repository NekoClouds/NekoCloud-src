package me.nekocloud.nekoapi.donatemenu;

import lombok.val;
import me.nekocloud.nekoapi.donatemenu.commands.DonateMenuCommand;
import me.nekocloud.nekoapi.donatemenu.commands.FastMessageCommand;
import me.nekocloud.nekoapi.donatemenu.commands.PrefixCommand;
import me.nekocloud.nekoapi.donatemenu.event.AsyncGamerSendFastMessageEvent;
import me.nekocloud.nekoapi.donatemenu.guis.DonateMenuGui;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DonateMenuListener extends DListener<NekoAPI> {

    private final Map<String, DonateMenuData> data = new ConcurrentHashMap<>();

    public DonateMenuListener(NekoAPI nekoAPI) {
        super(nekoAPI);

        new DonateMenuCommand(this);
        new PrefixCommand(this);
        new FastMessageCommand(this);
    }

    public void open(Player player, Class<? extends DonateMenuGui> clazz) {
        val name = player.getName().toLowerCase();
        DonateMenuData data = this.data.get(name);
        if (data == null) {
            data = new DonateMenuData(player);
            this.data.put(name, data);
        }

        val gui = data.get(clazz);
        if (gui == null) {
            return;
        }

        gui.open();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        data.remove(player.getName().toLowerCase());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSendFM(AsyncGamerSendFastMessageEvent e) {
        val gamer = e.getGamer();

        val fastMessage = e.getFastMessage();
        e.getRecipients().forEach(otherGamer -> sendFastMessage(gamer, otherGamer, fastMessage));
        sendFastMessage(gamer, GAMER_MANAGER.getSpigot(), fastMessage);
    }

    private void sendFastMessage(BukkitGamer gamer, GamerEntity gamerEntity, FastMessage fastMessage) {
        gamerEntity.sendMessage(" " + gamer.getChatName() + " §f✎§e "
                + gamerEntity.getLanguage().getMessage(fastMessage.getKey()) + " " + fastMessage.getSmile());
    }
}
