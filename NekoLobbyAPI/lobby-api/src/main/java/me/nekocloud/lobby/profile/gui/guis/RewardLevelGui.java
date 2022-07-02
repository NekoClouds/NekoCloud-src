package me.nekocloud.lobby.profile.gui.guis;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.sections.NetworkingSection;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.api.leveling.LevelRewardStorage;
import me.nekocloud.lobby.api.leveling.Leveling;
import me.nekocloud.lobby.api.profile.ProfileGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RewardLevelGui extends ProfileGui {

    private static final Leveling LEVELING = LobbyAPI.getLeveling();

    public RewardLevelGui(Player player) {
        super(player,"PROFILE_MAIN_ITEM_LEVEL_NAME");
    }

    @Override
    protected void setItems() {
        NetworkingSection section = gamer.getSection(NetworkingSection.class);
        List<LevelRewardStorage> rewards = LEVELING.getRewardsSorted();
        if (section == null || rewards.isEmpty()) {
            inventory.setItem(InventoryUtil.getSlotByXY(5, 3), new DItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName(lang.getMessage("LEVELING_LOAD_NAME"))
                    .setLore(lang.getList("LEVELING_LOAD_LORE"))
                    .build()));
            return;
        }

        val gamerLevel = gamer.getLevelNetwork();
        val giveRewardLevel = section.getGiveRewardLevel(); //за какой последний уровень выдана награда

        int slot = 11;
        int page = 0;
        for (val levelReward : rewards) { //они сразу будут отсортированы как надо
            int level = levelReward.getLevel();

            val chatColor = level > gamerLevel || level <= giveRewardLevel ? ChatColor.RED : ChatColor.GREEN;
            inventory.setItem(page, slot++, new DItem(ItemUtil.getBuilder(level <= giveRewardLevel ? Material.MINECART : Material.STORAGE_MINECART)
                    .setAmount(level)
                    .setName(chatColor.toString() + lang.getMessage("LEVEL_REWARD_NAME", level))
                    .setLore(lang.getList("LEVEL_REWARD_LORE1", level))
                    .addLore(levelReward.getLore(lang))
                    .addLore(level == giveRewardLevel + 1 ? lang.getList("LEVEL_REWARD_LORE2") : Collections.singletonList(" "))
                    .build(), (player, clickType, i) -> {
                        if (level > gamerLevel) {
                            gamer.sendMessageLocale("LEVEL_NO_LEVEL");
                            SOUND_API.play(player, SoundType.NO);
                            return;
                        }

                        if (level <= giveRewardLevel) {
                            gamer.sendMessageLocale("LEVEL_ALLREADY_GIVE");
                            SOUND_API.play(player, SoundType.NO);
                            return;
                        }

                        if (level != giveRewardLevel + 1) {
                            gamer.sendMessageLocale("LEVEL_NO_OTHER_REWARD");
                            SOUND_API.play(player, SoundType.NO);
                            return;
                        }

                        giveReward(section, levelReward);
                        SOUND_API.play(player, SoundType.SELECTED);
                        section.updateGiveRewardLevel();
                        update();
                    }));

            if ((slot - 7) % 9 == 0) {
                slot += 4;
            }

            if (slot >= 43) {
                slot = 11;
                page++;
            }
        }

        setBackItem();
        setGlassItems();

        INVENTORY_API.pageButton(lang, page + 1, inventory, 48, 50);
    }

    private void giveReward(NetworkingSection section, LevelRewardStorage levelReward) {
        if (section.getGiveRewardLevel() >= levelReward.getLevel()) {
            return;
        }

        levelReward.giveRewards(gamer);
    }
}
