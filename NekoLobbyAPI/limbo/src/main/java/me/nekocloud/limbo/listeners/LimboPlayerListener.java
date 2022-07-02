package me.nekocloud.limbo.listeners;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.limbo.NekoLimbo;
import me.nekocloud.limbo.data.LimboItem;
import me.nekocloud.limbo.data.LimboPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LimboPlayerListener implements Listener {

    private final Material hubItemType = Material.FIREBALL;
    private final int heldSlot = 4;

    private final TIntObjectMap<LimboItem> items = new TIntObjectHashMap<>();
    private final Map<String, LimboPlayer> players = new HashMap<>();

    private final NekoLimbo nekoLimbo;

    public LimboPlayerListener(NekoLimbo nekoLimbo) {
        this.nekoLimbo = nekoLimbo;
        for (val language : Language.values()) {
            items.put(language.getId(), new LimboItem(language, hubItemType));
        }
        Bukkit.getPluginManager().registerEvents(this, nekoLimbo);
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        val name = e.getName().toLowerCase();
        val limboPlayer = players.get(name);
        if (limboPlayer != null)
            return;

        players.put(name, new LimboPlayer(name));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        val player = e.getPlayer();
        val limboPlayer = players.get(player.getName().toLowerCase());
        if (limboPlayer == null)
            return;

        LimboItem limboItem = items.get(limboPlayer.getPlayerID());
        if (limboItem == null)
            limboItem = items.get(Language.DEFAULT.getId());

        player.getInventory().setHeldItemSlot(heldSlot);
        player.getInventory().setItem(heldSlot, limboItem.getItemStack());
    }

    @EventHandler
    public void changeHeldSlot(PlayerItemHeldEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getName().toLowerCase());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        val player = e.getPlayer();
        val itemHand = e.getItem();
        if (itemHand == null)
            return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        nekoLimbo.sendToHub(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        val player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null)
            return;

        if (!e.getClickedInventory().equals(player.getInventory()))
            return;

        if (e.getCurrentItem().getType() == Material.AIR)
            return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() != hubItemType)
            return;

        nekoLimbo.sendToHub(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage();
        if (command.startsWith("/hub") || command.startsWith("/lobby"))
            return;

        disableChat(e, e);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        disableChat(e, e);
    }

    private void disableChat(PlayerEvent e, Cancellable cancellable) {
        val player = e.getPlayer();
        cancellable.setCancelled(true);
        val limboPlayer = players.get(player.getName().toLowerCase());

        if (limboPlayer == null)
            return;

        val language = limboPlayer.getLanguage();
        player.sendMessage(language.getMessage("LIMBO_NO_CHAT"));
    }

}
