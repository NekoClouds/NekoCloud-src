package me.nekocloud.packetlib.nms.v1_12_R1.gui;

import me.nekocloud.packetlib.nms.interfaces.gui.DEnchantingTable;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.packetlib.nms.types.EnchantingSlot;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DEnchantingTableImpl implements DEnchantingTable {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private EntityPlayer entityPlayer;
    private String title;
    private EnchantingTableContainer container;

    public DEnchantingTableImpl(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Language lang = gamer.getLanguage();
        this.title = lang.getMessage("ENCHANT_TITLE");
        this.entityPlayer = ((CraftPlayer)player).getHandle();
        this.container = new EnchantingTableContainer(this.entityPlayer);
    }

    @Override
    public void addItem(EnchantingSlot slot, ItemStack stack) {
        this.container.setItem(slot.getSlot(), CraftItemStack.asNMSCopy(stack));
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void openGui() {
        if (entityPlayer == null)
            return;

        int c = this.entityPlayer.nextContainerCounter();

        this.entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:enchanting_table",
                IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + this.title + "\"}")));
        this.entityPlayer.activeContainer = this.container;
        this.entityPlayer.activeContainer.windowId = c;
        this.entityPlayer.activeContainer.addSlotListener(this.entityPlayer);
    }

    private class EnchantingTableContainer extends ContainerEnchantTable {

        EnchantingTableContainer(EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0));
        }

        @Override
        public boolean canUse(EntityHuman entityhuman) {
            return true;
        }
    }
}
