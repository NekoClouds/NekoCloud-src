package me.nekocloud.nekoapi.donatemenu.guis;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.nekoapi.donatemenu.DonateMenuData;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class PrefixGui extends DonateMenuGui {

    private static final ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();

    public PrefixGui(Player player, DonateMenuData donateMenuData, Language language) {
        super(player, donateMenuData, "§0➲ §0§n" + language.getMessage("DONATE_MENU_NAME")
                + "§r §0| " + language.getMessage("DONATE_MENU_PREFIX_NAME"));
    }

    @Override
    protected void setItems(BukkitGamer gamer) {
        setGlassItems();

        createItem(gamer,20, ChatColor.GRAY, 8);
        createItem(gamer,21, ChatColor.YELLOW, 4);
        createItem(gamer,22, ChatColor.AQUA, 3);
        createItem(gamer,23, ChatColor.GREEN, 5);
        createItem(gamer,24, ChatColor.RED, 14);
        createItem(gamer,29, ChatColor.DARK_GREEN, 13);
        createItem(gamer,30, ChatColor.GOLD, 1);
        createItem(gamer,31, ChatColor.DARK_AQUA, 9);
        createItem(gamer,32, ChatColor.LIGHT_PURPLE, 6);

        setBack(donateMenuData.get(MainDonateMenuGui.class));
    }

    private void createItem(BukkitGamer owner, int slot, ChatColor color, int blockColorId) {
        boolean available = owner.getPrefixColor() == color;
        val lang = owner.getLanguage();
        val nameColor = "§" + color.getChar() + lang.getMessage("COLOR_" + color.name().toUpperCase());
        inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                .setDurability((short) blockColorId)
                .setName(nameColor)
                .setLore(available ?
                        lang.getList("PREFIX_CHANGE_LORE2") :
                        lang.getList("PREFIX_CHANGE_LORE", nameColor))
                .removeFlags()
                .glowing(available)
                .build(), (clicker, clickType, slot1) -> {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(clicker);
            if (gamer == null || (gamer.getGroup() != Group.NEKO && gamer.getGroup().getLevel() < Group.ADMIN.getLevel()) || gamer.isTester()) {
                player.sendMessage(lang.getMessage("NO_PERMS_NEKO_ONLY"));
                SOUND_API.play(player, SoundType.NO);
                return;
            }

            if (available) {
                SOUND_API.play(player, SoundType.NO);
                return;
            }

            setPrefix(gamer, color);
            player.closeInventory();
        }));
    }

    private static void setPrefix(BukkitGamer gamer, ChatColor chatColor) {
        if (NekoCloud.isGame()) {
            gamer.sendMessageLocale("NO_PREFIX_SET");
            SOUND_API.play(gamer.getPlayer(), SoundType.NO);
            return;
        }

        SOUND_API.play(gamer.getPlayer(), SoundType.SELECTED);
        gamer.sendMessageLocale("PREFIX_SET", chatColor.name().toUpperCase());

        val newPrefix = gamer.getPrefix().replaceAll("§[0-9a-e]", "§" + chatColor.getChar());
        if (newPrefix.equalsIgnoreCase(gamer.getPrefix()))
            return;

        gamer.setPrefix(newPrefix);
        BukkitUtil.runTaskAsync(() -> GlobalLoader.setPrefix(gamer.getPlayerID(), "§" + chatColor.getChar()));
        SCORE_BOARD_API.setPrefix(gamer.getPlayer(), newPrefix);
    }
}
