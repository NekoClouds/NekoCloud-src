package me.nekocloud.skyblock.gui.guis;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.skyblock.api.SkyBlockGui;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.member.IslandMember;
import me.nekocloud.skyblock.api.island.member.MemberType;
import me.nekocloud.skyblock.gui.AcceptGui;
import me.nekocloud.skyblock.gui.GuiUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MembersGui extends SkyBlockGui {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final ItemStack RED_PANE = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
            .setDurability((short) 14)
            .build();

    private final MultiInventory inventory;

    public MembersGui(Player player) {
        super(player, "");
        inventory = API.createMultiInventory(player,
                lang.getMessage(GUI_NAME_SKY_BLOCK) + " ▸ "
                   + lang.getMessage("ISLAND_MEMBERS_GUI_NAME"), 5);
    }

    @Override
    protected void setItems(Player clicker) {
        Island island = ISLAND_MANAGER.getIsland(clicker);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(clicker);
        if (gamer == null || island == null || inventory == null)
            return;

        inventory.clearInventories();

        GuiUtil.setBack(inventory, lang);

        int slot = 10;
        int page = 0;

        MemberType memberType = island.getMemberType(gamer);
        for (IslandMember member : island.getMembers()) {
            IBaseGamer iBaseGamer = member.getGamer();
            boolean online = iBaseGamer.isOnline();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(member.getDate().getTime());
            String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

            String rank = lang.getMessage(member.getType().getKey());

            ItemStack head = Head.getHeadByValue(iBaseGamer.getSkin().getValue());
            ItemStack item = ItemUtil.getBuilder(online ? head : RED_PANE)
                    .setName(iBaseGamer.getDisplayName())
                    .setLore(lang.getList("ISLAND_MEMBER_LORE",
                            date, rank, getCord(member)))
                    .build();

            inventory.setItem(page, slot, new DItem(item, (player, clickType, i) -> {
                if (!clickType.isRightClick())
                    return;

                if (memberType == MemberType.MEMBER || memberType == MemberType.NOBODY) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    return;
                }

                if (member.getType() == MemberType.OWNER) {
                    gamer.sendMessageLocale("ISLAND_MEMBER_SETTINGS_OWNER");
                    return;
                }
                IslandMemberGui islandMemberGui = new IslandMemberGui(clicker, member, item);
                islandMemberGui.open();

            }));

            slot++;

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        API.pageButton(lang, page + 1, inventory, 38, 42);
    }

    private String getCord(IslandMember member) {
        Player player = member.getPlayer();
        if (player == null)
            return lang.getMessage("ISLAND_MEMBER_OFFLINE");

        Location location = player.getLocation();
        if (location.getWorld().getName().equals("lobby"))
            return "§cSpawn";

        String nameOwner = "§cN/A";

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island != null)
            nameOwner = island.getOwner().getName();

        return "§e" + nameOwner;
    }

    @Override
    public void open() {
        inventory.openInventory(player);
    }

    private class IslandMemberGui {
        private final Player player;
        private final IslandMember islandMember;
        private final ItemStack head;

        private Language lang;
        private DInventory inventory;

        private IslandMemberGui(Player player, IslandMember islandMember, ItemStack head) {
            this.player = player;
            this.islandMember = islandMember;
            this.head = head.clone();

            ItemMeta meta = head.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            meta.setLore(lore);
            this.head.setItemMeta(meta);

            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                return;

            this.lang = gamer.getLanguage();
            this.inventory = API.createInventory(player, islandMember.getName(), 5);
            setItems();
        }

        private void setItems() {
            if (inventory == null)
                return;
            inventory.clearInventory();

            GuiUtil.setBack(inventory, lang);
            inventory.setItem(4, new DItem(head));

            inventory.setItem(9 * 2 + 1, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                    .setDurability((short) 5)
                    .setName("§a" + lang.getMessage(MemberType.MEMBER.getKey()))
                    .setLore(lang.getList( "ISLAND_RANK_MEMBER"))
                    .build()));
            inventory.setItem(9 * 2 + 2, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                    .setDurability((short) 6)
                    .setName("§a" + lang.getMessage(MemberType.CO_OWNER.getKey()))
                    .setLore(lang.getList("ISLAND_RANK_CO_OWNER"))
                    .build()));

            boolean member = islandMember.getType() == MemberType.MEMBER;
            inventory.setItem(9 * 3 + 1, new DItem(ItemUtil.getBuilder(Material.INK_SACK)
                    .setDurability((short) (member ? 10 : 8))
                    .setName((member ?
                            lang.getMessage("ISLAND_MEMBER_SET_RANK_NAME_ON") :
                            lang.getMessage("ISLAND_MEMBER_SET_RANK_NAME_OFF")))
                    .setLore((member ?
                            lang.getList("ISLAND_MEMBER_SET_RANK_LORE_ON") :
                            lang.getList("ISLAND_MEMBER_SET_RANK_LORE_OFF")))
                    .build(), (player, clickType, i) -> {
                if (islandMember.getType() == MemberType.MEMBER)
                    return;
                islandMember.setMemberType(MemberType.MEMBER);
                islandMember.getIsland().broadcastMessageLocale("ISLAND_MEMBER_SET_RANK_ALERT",
                        islandMember.getGamer().getDisplayName());
                player.closeInventory();
            }));
            inventory.setItem(9 * 3 + 2, new DItem(ItemUtil.getBuilder(Material.INK_SACK)
                    .setDurability((short) (!member ? 10 : 8))
                    .setName((!member ?
                            lang.getMessage("ISLAND_MEMBER_SET_RANK_NAME_ON") :
                            lang.getMessage("ISLAND_MEMBER_SET_RANK_NAME_OFF")))
                    .setLore((!member ?
                            lang.getList("ISLAND_MEMBER_SET_RANK_LORE_ON") :
                            lang.getList("ISLAND_MEMBER_SET_RANK_LORE_OFF")))
                    .build(), (player, clickType, i) -> {
                if (islandMember.getType() == MemberType.CO_OWNER)
                    return;
                islandMember.setMemberType(MemberType.CO_OWNER);
                islandMember.getIsland().broadcastMessageLocale("ISLAND_MEMBER_SET_RANK_ALERT",
                        islandMember.getGamer().getDisplayName());
                player.closeInventory();
            }));

            inventory.setItem(9 * 2 + 6, new DItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName("§a" + lang.getMessage("ISLAND_MEMBER_REMOVE_NAME"))
                    .setLore(lang.getList("ISLAND_MEMBER_REMOVE_LORE"))
                    .build(), (player, clickType, i) -> new AcceptGui(player, AcceptGui.Type.REMOVE_MEMBER).open(()-> {
                        player.chat("/island remove " + islandMember.getName());
                        player.closeInventory();
                    }, ()-> {
                        MembersGui gui = SKY_GUI_MANAGER.getGui(MembersGui.class, player);
                        if (gui == null)
                            return;
                        gui.open();
                    })));
        }

        public void open() {
            if (inventory == null)
                return;

            inventory.openInventory(player);
        }
    }
}
