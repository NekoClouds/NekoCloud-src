package me.nekocloud.survival.commons.commands.warp;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PlayerWarpCommand extends CommonsCommand {

    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private final WarpManager warpManager = CommonsSurvivalAPI.getWarpManager();
    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    public PlayerWarpCommand(ConfigData configData) {
        super(configData, true, "playerwarp", "playerwarps");
        spigotCommand.setCooldown(20, "playerwarpcooldown");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity,"SERVER_PREFIX",  "PLAYER_WARP_FORMAT");
            return;
        }

        BukkitUtil.runTaskAsync(() -> {
            IBaseGamer owner = gamerManager.getOrCreate(args[0]);
            if (owner == null) {
                COMMANDS_API.playerNeverPlayed(gamerEntity, args[0]);
                return;
            }

            List<Warp> warpList = warpManager.getWarps(owner.getPlayerID());
            PlayerWarpGui playerWarpGui = new PlayerWarpGui(player, owner, gamer.getLanguage());
            playerWarpGui.initGui(warpList, gamer);
            BukkitUtil.runTask(() -> playerWarpGui.open(player));
        });
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class PlayerWarpGui {

        MultiInventory inventory;
        IBaseGamer owner;
        Language lang;

        PlayerWarpGui(Player player, IBaseGamer owner, Language language) {
            this.owner = owner;
            this.lang = language;
            this.inventory = INVENTORY_API.createMultiInventory(player,
                    language.getMessage("PLAYER_WARP_GUI_NAME", owner.getName()), 5);
        }

        void initGui(List<Warp> warps, BukkitGamer mainGamer) {
            if (warps.isEmpty()) {
                inventory.setItem(0, 22, new DItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                        .setName(lang.getMessage("PLAYER_WARP_ITEM_EMPTY_NAME"))
                        .setLore(lang.getList("PLAYER_WARP_ITEM_EMPTY_LORE", owner.getDisplayName()))
                        .build(), (player, clickType, slot1) -> SOUND_API.play(player, SoundType.TELEPORT)));
                return;
            }

            boolean friend = mainGamer.getFriends().containsKey(owner.getPlayerID());

            int slot = 10;
            int page = 0;
            for (Warp warp : warps){
                if (warp.isPrivate() && !friend)  {
                    continue;
                }

                val calendar = Calendar.getInstance();
                calendar.setTimeInMillis(warp.getDate().getTime());
                String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

                String name = warp.getName();
                Location location = warp.getLocation();
                int players = warp.getNearbyPlayers(30).size();

                inventory.setItem(page, slot, new DItem(ItemUtil.getBuilder(warp.getIcon())
                        .setName("§a" + name)
                        .setLore(lang.getList( "WARP_ITEM_LORE",
                                warp.getNameOwner(),
                                date,
                                location.getWorld().getName(),
                                String.valueOf((int) location.getX()),
                                String.valueOf((int) location.getY()),
                                String.valueOf((int) location.getZ()),
                                String.valueOf(players),
                                CommonWords.PLAYERS_1.convert(players, lang)))
                        .build(), (player, clickType, i) -> {
                    player.chat("/warp " + name);
                    SOUND_API.play(player, SoundType.SELECTED);
                    player.closeInventory();
                }));

                slot++;

                if ((slot - 8) % 9 == 0)
                    slot += 2;

                if (slot >= 35) {
                    slot = 10;
                    page++;
                }
            }

            INVENTORY_API.pageButton(lang, page + 1, inventory, 38, 42);
        }

        void open(Player player) {

            if (inventory != null) {
                inventory.openInventory(player);
            }
        }
    }
}
