package me.nekocloud.skyblock.listener;

import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.Warp;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.api.manager.WarpManager;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.TitleAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.skyblock.SkyBlock;
import me.nekocloud.skyblock.api.event.IslandAsyncCreateEvent;
import me.nekocloud.skyblock.api.event.IslandAsyncRemoveEvent;
import me.nekocloud.skyblock.api.event.IslandRemoveMemberEvent;
import me.nekocloud.skyblock.api.event.absract.IslandListener;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.member.IslandMember;
import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.skyblock.module.HomeModule;
import me.nekocloud.skyblock.utils.FaweUtils;
import me.nekocloud.skyblock.utils.ItemsContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class IslandMainListener extends IslandListener {

    private final TitleAPI titleAPI = NekoCloud.getTitlesAPI();
    private final UserManager userManager = CommonsSurvivalAPI.getUserManager();

    private static final WarpManager WARP_MANAGER = CommonsSurvivalAPI.getWarpManager();

    public IslandMainListener(SkyBlock skyBlock) {
        super(skyBlock);
    }

    @EventHandler
    public void onCreateIsland(IslandAsyncCreateEvent e) throws IOException {
        Player player = e.getPlayer();
        Island island = e.getIsland();
        IslandTerritory territory = island.getTerritory();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }

        Language lang = gamer.getLanguage();
        Location locationHome = island.getTerritory().getMiddleChunk().getMiddle();

        Group group = gamer.getGroup();
        String schematicName = island.getIslandType().getNameFile();
        List<ItemStack> items = ItemsContainer.getItems(group).getItems();
        FaweUtils.pasteSchematic(locationHome, schematicName, items);
        Biome biome = island.getIslandType().getBiome();
        FaweUtils.setBiome(territory, biome);

        island.getModule(HomeModule.class).setHome(locationHome);

        BukkitUtil.runTaskLater(20L, () -> {
            if (user.teleport(locationHome)) {
                locationHome.getWorld().spawnEntity(locationHome, EntityType.COW);

                titleAPI.sendTitle(player,
                        lang.getMessage("ISLAND_CREATE_TITLE"),
                        lang.getMessage("ISLAND_CREATE_SUBTITLE"));
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onGrow(BlockSpreadEvent e) { //todo запретить распространение хоруса и других блоков на чужую территорию
        Block source = e.getSource(); //с которого началось
        Block block = e.getBlock(); //какой блок изменился
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoveIsland(IslandAsyncRemoveEvent event) {
        Island island = event.getIsland();

        Location spawn = CommonsSurvivalAPI.getSpawn();
        for (IslandMember islandMember : island.getMembers()) {
            IBaseGamer iBaseGamer = islandMember.getGamer();
            if (!iBaseGamer.isOnline()) {
                continue;
            }

            BukkitGamer gamer = (BukkitGamer) iBaseGamer;

            Player player = gamer.getPlayer();
            if (gamer.getPlayerID() != island.getOwner().getPlayerID())
                gamer.sendMessageLocale("ISLAND_REMOVED_MEMBER");

            BukkitUtil.runTask(() -> player.teleport(spawn));
        }

        IslandTerritory territory = island.getTerritory();
        removedWarps(territory);

        island.delete();
    }

    static void removedWarps(IslandTerritory territory) {
        BukkitUtil.runTaskAsync(() -> {
            for (Warp warp : WARP_MANAGER.getWarps().values()) { //удаление варпов c места, где удалили остров
                if (!isSkyBlockWorld(warp.getWorld()) || !territory.canInteract(warp.getLocation())) {
                    continue;
                }

                IBaseGamer ownerWarp = warp.getOwner();
                BukkitGamer gamer = GAMER_MANAGER.getGamer(ownerWarp.getPlayerID());
                if (gamer != null)
                    gamer.sendMessageLocale("ISLAND_WARP_REMOVED", warp.getName());

                warp.remove();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRemovePlayer(IslandRemoveMemberEvent e) {  //при удалении с острова игрока - удалить его варпы
        Island island = e.getIsland();
        IslandTerritory territory = island.getTerritory();
        int removedPlayer = e.getMemberID();

        for (Warp warp : WARP_MANAGER.getWarps().values()) {
            if (!isSkyBlockWorld(warp.getWorld()))
                continue;
            if (warp.getOwnerID() != removedPlayer)
                continue;
            if (!territory.canInteract(warp.getLocation()))
                continue;

            warp.remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) //кликаешь по обсидиану пустым ведром и у тебя лава в ведре
    public void onObsidianToLava(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!isSkyBlockWorld(player.getWorld()))
            return;

        Block block = e.getClickedBlock();
        if (block.getType() != Material.OBSIDIAN)
            return;

        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.BUCKET)
            return;

        Island island = ISLAND_MANAGER.getIsland(player);
        if (island == null || !island.hasMember(player))
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        if (Cooldown.hasCooldown(gamer, "obsidian_to_lava")){
            Language lang = gamer.getLanguage();
            int time = Cooldown.getSecondCooldown(gamer, "obsidian_to_lava");
            gamer.sendMessageLocale("COOLDOWN", String.valueOf(time),
                    CommonWords.SECONDS_1.convert(time, lang));
            return;
        }
        Cooldown.addCooldown(gamer, "obsidian_to_lava", 20L * 30);

        block.setType(Material.AIR);
        item.setAmount(item.getAmount() - 1);
        player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
        e.setCancelled(true);
    }

    //todo сделать чтобы игроки не застревали в порталах в АД
    //todo запретить выходить за территорию своего острова
}
