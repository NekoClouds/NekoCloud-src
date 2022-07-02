package me.nekocloud.lobby.profile.gui.guis;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.gamer.sections.NetworkingSection;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.lobby.api.profile.ProfileGui;
import me.nekocloud.nekoapi.donatemenu.guis.MainDonateMenuGui;
import me.nekocloud.nekoapi.guis.basic.DonateGui;
import me.nekocloud.nekoapi.guis.basic.HelpGui;
import me.nekocloud.nekoapi.guis.basic.SitesGui;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileMainPage extends ProfileGui {

    private static final NekoAPI DARTA_API = JavaPlugin.getPlugin(NekoAPI.class);

    public ProfileMainPage(Player player) {
        super(player);
    }

    @Override
    protected void setItems() {
        setGlassItems();


        // Так надо :(
        val playtime = TimeUtil.leftTime(lang,
                DARTA_API.getPlayTimeManager().getTimeFromMap(gamer.getPlayerID()),
                "TIME_SECOND_1", "TIME_MINUTES_1", "TIME_HOURS_1", "TIME_DAY_1");
        // Основной профиль
        val skinName = gamer.getSkin() == Skin.DEFAULT_SKIN ? "N/A" : gamer.getSkin().getSkinName();
        val groupName = gamer.getGroup().getLocaleName(lang);

//        val authPlayer = AuthModule.getInstance().getAuthPlayer(gamer.getPlayerID());

        String vkId = "§cНе привязано";
        String discordTag = "§cНе привязано";

//        if (authPlayer.hasVKUser())
//            vkId = "@id" + authPlayer.getVkId();
//
//        if (authPlayer.hasDiscordUser())
//            discordTag = authPlayer.getDiscordTag();

        inventory.setItem(13, new DItem(ItemUtil.getBuilder(gamer.getHead())
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_PLAYER_NAME"))
                .setLore(lang.getList( "PROFILE_MAIN_ITEM_PLAYER_LORE",
                        gamer.getName(),
                        groupName,
                        gamer.getLanguage().getName(),
                        vkId,
                        discordTag,
                        StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.COINS)),
                        StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.VIRTS)),
                        String.valueOf(gamer.getFriends().size()),
                        String.valueOf(gamer.getFriendsLimit()),
                        gamer.getVersion().toClientName(),
                        skinName,
                        playtime
                ))
                .build(), (pl, clickType, slot) -> {
            val statsGui = GUI_MANAGER.getGui(ProfileStatsGui.class, pl);
            if (statsGui == null) {
                return;
            }
            statsGui.open();
        }));

        // Уровни
        inventory.setItem(22, new DItem(ItemUtil.getBuilder(Material.BREWING_STAND_ITEM)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_LEVEL_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_LEVEL_LORE",
                        StringUtil.getNumberFormat(gamer.getLevelNetwork()),
                        StringUtil.onPercentBar(NetworkingSection.getCurrentXPLVL(gamer.getExp()),
                                NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1)),
                        StringUtil.onPercent(NetworkingSection.getCurrentXPLVL(gamer.getExp()),
                                NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1)) + "%",
                        StringUtil.getNumberFormat(gamer.getExpNextLevel())
                )).build(), (pl, clickType, slot) -> {
            val levelGui = GUI_MANAGER.getGui(RewardLevelGui.class, pl);
            if (levelGui == null) {
                return;
            }
            levelGui.open();
        }));

        // Настройки
        val enable =  "§a" + lang.getMessage("ENABLE");
        val disable =  "§c" + lang.getMessage( "DISABLE");
        inventory.setItem(31, new DItem(ItemUtil.getBuilder(Material.REDSTONE_COMPARATOR)
                .setName("§b" + lang.getMessage( "PROFILE_MAIN_ITEM_SETTINGS_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_SETTINGS_LORE",
                        (gamer.getSetting(SettingsType.BLOOD) ? enable : disable),
                        (gamer.getSetting(SettingsType.FLY) ? enable : disable),
                        (gamer.getSetting(SettingsType.HIDER) ? enable : disable),
                        (gamer.getSetting(SettingsType.CHAT) ? enable : disable),
                        (gamer.getSetting(SettingsType.MUSIC) ? enable : disable),
                        (gamer.getSetting(SettingsType.BOARD) ? enable : disable),
                        (gamer.getSetting(SettingsType.PRIVATE_MESSAGE) ? enable : disable),
                        (gamer.getSetting(SettingsType.FRIENDS_REQUEST) ? enable : disable),
                        (gamer.getSetting(SettingsType.PARTY_REQUEST) ? enable : disable),
                        (gamer.getSetting(SettingsType.GUILD_REQUEST) ? enable : disable),
                        (gamer.getSetting(SettingsType.DONATE_CHAT) ? enable : disable)
                )).build(), (pl, clickType, slot) -> {
            SettingsPage settingsPage = GUI_MANAGER.getGui(SettingsPage.class, pl);
            if (settingsPage == null) {
                return;
            }
            settingsPage.open();
        }));

        // Меню помощи
        inventory.setItem(50, new DItem(ItemUtil.getBuilder(Material.BOOK_AND_QUILL)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_INFO_NAME"))
                .setLore(lang.getList( "PROFILE_MAIN_ITEM_INFO_LORE"))
                .build(), (pl, clickType, slot) ->
                DARTA_API.getGuiDefaultContainer().openGui(HelpGui.class, pl)));

        // Меню с привилегиями 
        inventory.setItem(49, new DItem(ItemUtil.getBuilder(Head.DONATE)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_DONATE_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_DONATE_LORE"))
                .build(), (pl, clickType, slot) ->
                DARTA_API.getGuiDefaultContainer().openGui(DonateGui.class, pl)));
        
        // Сайты
        inventory.setItem(48, new DItem(ItemUtil.getBuilder(Head.MAILBOX)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_DONATE_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_DONATE_LORE"))
                .build(), (pl, clickType, slot) ->
                DARTA_API.getGuiDefaultContainer().openGui(SitesGui.class, pl)));

        // Локализация
        List<String> lore = new ArrayList<>(lang.getList("PROFILE_MAIN_ITEM_LANG_LORE1"));
        Arrays.stream(Language.values()).forEach(localization ->
                lore.add("§f ▪ " + (lang == localization ? "§a" : "")
                + localization.getName() + " §7(§c" + localization.getPercent() + "%§7)" ));
        lore.addAll(lang.getList( "PROFILE_MAIN_ITEM_LANG_LORE2"));
        inventory.setItem(23, new DItem(ItemUtil.getBuilder(Head.WORLD)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_LANG_NAME"))
                .setLore(lore)
                .build(), (pl, clickType, slot) -> {
            val langPage = GUI_MANAGER.getGui(LangPage.class, pl);
            if (langPage == null) {
                return;
            }
            langPage.open();
        }));

        // Донат меню
        inventory.setItem(21, new DItem(ItemUtil.getBuilder(Head.DONATE_MENU)
                .setName("§b" + lang.getMessage("DONATE_MENU_NAME"))
                .setLore(lang.getList("DONATE_MENU_LORE", gamer.getChatName()))
                .removeFlags()
                .build(), (pl, clickType, slot) -> DARTA_API.getDonateMenuListener().open(pl, MainDonateMenuGui.class)));

        // ???
        inventory.setItem(32, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                .setDurability((short) 14)
                .setName("§c???")
                .build()));

        // Косметика ибаная
        inventory.setItem(30, new DItem(ItemUtil.getBuilder(Head.GADGETS_ITEM)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_COSMETIC_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_COSMETIC_LORE"))
                .build(), (player1, clickType, i) -> player1.chat("/cosmetics")));
    }

}
