package me.nekocloud.nekoapi.donatemenu.guis;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.JoinMessage;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.donatemenu.DonateMenuData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class JoinMessageGui extends DonateMenuGui {

    public JoinMessageGui(Player player, DonateMenuData donateMenuData, Language language) {
        super(player, donateMenuData, "§0➲ §0§n" + language.getMessage("DONATE_MENU_NAME")
                + "§r §0| " + language.getMessage("DONATE_MENU_JOIN_MESSAGE_NAME"));
    }

    @Override
    protected void setItems(BukkitGamer gamer) {
        setBack(donateMenuData.get(MainDonateMenuGui.class));
        setGlassItems();

        val language = gamer.getLanguage();
        List<JoinMessage> messages = JoinMessage.getMessages();

        int slot = 11;
        int page = 0;
        for (val joinMessage : messages) {
            val avalaible = gamer.getAvailableJoinMessages().contains(joinMessage);
            val active = gamer.getJoinMessage() == joinMessage;

            inventory.setItem(page, slot++, new DItem(ItemUtil
                    .getBuilder(getItem(joinMessage, avalaible, active, language, gamer.getDisplayName()))
                    .build(), (clicker, clickType, clickedSlot) -> {
                if (avalaible && active) {
                    SOUND_API.play(clicker, SoundType.NO);
                    return;
                }

                if (avalaible) {
                    gamer.setDefaultJoinMessage(joinMessage);
                    SOUND_API.play(clicker, SoundType.CHEST_OPEN);
                    gamer.sendMessageLocale("DONATE_MENU_JOIN_MESSAGE_ENABLE",
                            StringUtil.getNumberFormat(joinMessage.getId()));
                    clicker.closeInventory();
                    donateMenuData.updateClass(this, clicker, language);
                    return;
                }

                if (joinMessage.getPriceGold() <= 0) {
                    SOUND_API.play(clicker, SoundType.NO);
                    return;
                }

                if (gamer.changeMoney(PurchaseType.VIRTS, -joinMessage.getPriceGold())) {
                    gamer.addJoinMessage(joinMessage);
                    SOUND_API.play(clicker, SoundType.LEVEL_UP);
                    clicker.closeInventory();
                    donateMenuData.updateClass(this, clicker, language);
                    gamer.sendMessageLocale("DONATE_MENU_JOIN_MESSAGE_BUY",
                            StringUtil.getNumberFormat(joinMessage.getId()));
                    return;
                }

                SOUND_API.play(clicker, SoundType.NO);
            }));

            if ((slot - 7) % 9 == 0) {
                slot += 4;
            }

            if (slot >= 43) {
                slot = 11;
                page++;
            }
        }

        INVENTORY_API.pageButton(language, InventoryUtil.getPagesCount(messages.size(), 21),
                inventory, 48, 50);
    }

    private static ItemStack getItem(JoinMessage joinMessage, boolean available, boolean active,
                                     Language language, String displayName) {
        List<String> dependLore = new ArrayList<>();
        if (!available && joinMessage.can()) {
            if (joinMessage.isNeko()) {
                dependLore.add("");
                dependLore.add("§7" + language.getMessage("GADGETS_CAN_AVAILABLE"));
                if (joinMessage == JoinMessage.DEFAULT_MESSAGE) {
                    dependLore.add("§8• " + language.getMessage("GADGETS_GIVE_DONATE", Group.HEGENT.getNameEn()));
                } else {
                    dependLore.add("§8• " + language.getMessage("GADGETS_GIVE_DONATE_ONLY", Group.NEKO.getNameEn()));
                }
            }
            if (joinMessage.getPriceGold() > 0) {
                dependLore.add("");
                dependLore.add(language.getMessage("GADGETS_GIVE_GOLD",
                        StringUtil.getNumberFormat(joinMessage.getPriceGold())));
            }
        }
        dependLore.add("");
        if (active) {
            dependLore.add(language.getMessage("JOIN_MESSAGE_LORE_SELECT"));
        } else if (available){
            dependLore.add(language.getMessage("JOIN_MESSAGE_LORE_NOT_SELECT"));
        } else {
            dependLore.add(language.getMessage("JOIN_MESSAGE_LORE_NOT_AVAILABLE"));
        }
        String message = joinMessage == JoinMessage.DEFAULT_MESSAGE ?
                language.getMessage("JOIN_PLAYER_LO_LOBBY", displayName) :
                String.format(joinMessage.getMessage(), displayName);
        return ItemUtil.getBuilder(available || active ? new ItemStack(Material.PAPER) : NO_PERMS)
                .setName((active ? "§b" : (available ? "§a" : "§c"))
                        + language.getMessage("JOIN_MESSAGE_NAME", String.valueOf(joinMessage.getId())))
                .setLore(language.getList("JOIN_MESSAGE_LORE", message))
                .addLore(dependLore)
                .glowing(active)
                .build();
    }
}
