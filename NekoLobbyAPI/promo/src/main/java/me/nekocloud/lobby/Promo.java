package me.nekocloud.lobby;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.map.MapPalette;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class Promo extends JavaPlugin implements Listener {

    private WrapperPlayServerMap mapPacket;
    private byte[] bytes;

    @Override
    public void onEnable() {
        val spigot = NekoCloud.getGamerManager().getSpigot();
        if (spigot == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!loadImage(spigot)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.MAP) {
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                packet.getByteArrays().write(0, bytes);
                packet.getIntegers().write(3, 128);
                packet.getIntegers().write(4, 128);
            }
        });

    }

    private boolean loadImage(Spigot spigot) {
        val imgFile = new File(this.getDataFolder(), "image.png");
        if (!imgFile.exists()) {
            spigot.sendMessage("§c[PROMO] Картинка не найдена! плагин отключается!");
            return false;
        }

        BufferedImage img;
        try {
            img = ImageIO.read(imgFile);
        } catch (IOException e) {
            return false;
        }

        if (img.getHeight() != 128 || img.getWidth() != 128) {
            spigot.sendMessage("§c[PROMO] ТЫ ЕБАН?! Разрешение должно быть 128*128");
            return false;
        }

        bytes = MapPalette.imageToBytes(img);

        mapPacket = new WrapperPlayServerMap();
        mapPacket.setScale((byte) 3);
        mapPacket.setColumns(128);
        mapPacket.setRows(128);
        mapPacket.setX(0);
        mapPacket.setZ(0);
        mapPacket.setTrackingPosition(false);
        mapPacket.setData(bytes);
        mapPacket.setItemDamage(0);

        return true;

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();

        setItem(gamer, gamer.getLanguage());
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        setItem(e.getGamer(), e.getLanguage());
    }

    private void setItem(BukkitGamer gamer, Language lang) {
        val player = gamer.getPlayer();
        if (player == null) {
            return;
        }
        mapPacket.sendPacket(player);
        player.getInventory().setItemInOffHand(ItemUtil.getBuilder(Material.MAP)
                .setName(lang.getMessage("PROMO_ITEM_NAME"))
                .removeFlags()
                .build());
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getSlotType() != InventoryType.SlotType.QUICKBAR || e.getSlot() != 40) {
            return;
        }

        e.setCancelled(true);
    }
}
