package me.nekocloud.skyblock.listener;

import lombok.val;
import me.nekocloud.api.BorderAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.skyblock.SkyBlock;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.entity.SkyGamer;
import me.nekocloud.skyblock.api.event.IslandAsyncCreateEvent;
import me.nekocloud.skyblock.api.event.absract.IslandListener;
import me.nekocloud.skyblock.api.event.module.IslandUpgradeEvent;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.member.IslandMember;
import me.nekocloud.skyblock.api.manager.EntityManager;
import me.nekocloud.skyblock.api.manager.SkyGamerManager;
import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.skyblock.module.BorderModule;
import me.nekocloud.skyblock.module.GeneratorModule;
import me.nekocloud.skyblock.module.IgnoreModule;
import me.nekocloud.survival.commons.api.events.UserTeleportByCommandEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ModuleListener extends IslandListener {

    private final BorderAPI borderAPI = NekoCloud.getBorderAPI();
    private final SkyGamerManager manager = SkyBlockAPI.getSkyGamerManager();
    private final EntityManager entityManager = SkyBlockAPI.getEntityManager();

    public ModuleListener(SkyBlock skyBlock) {
        super(skyBlock);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreateIsland(IslandAsyncCreateEvent e) {
        Island island = e.getIsland();
        BorderModule module = island.getModule(BorderModule.class);
        if (module == null)
            return;

        BukkitGamer gamer = (BukkitGamer) island.getOwner();
        if (gamer == null)
            return;

        Player player = gamer.getPlayer();
        if (player == null)
            return;

        sendBoarder(player, island);
    }

    @EventHandler
    public void onUpgradeBorder(IslandUpgradeEvent e) {
        Island island = e.getIsland();
        BorderModule module = island.getModule(BorderModule.class);
        if (module == null) {
            return;
        }

        IslandTerritory territory = island.getTerritory();
        Location middle = territory.getMiddleChunk().getMiddle();
        int finalSize = (((module.getSize() + 2) / 2) * 16 + 8) * 2;

        new BukkitRunnable() {
            int size = ((module.getSize() / 2) * 16 + 8) * 2;
            List<Player> players = entityManager.getPlayers(territory);

            @Override
            public void run() {
                size += 1;

                for (IslandMember islandMember : island.getMembers()) {
                    int playerID = islandMember.getPlayerID();

                    SkyGamer skyGamer = manager.getSkyGamer(playerID);
                    if (skyGamer == null || !skyGamer.isBorder())
                        continue;

                    BukkitGamer gamer = GAMER_MANAGER.getGamer(skyGamer.getName());
                    if (gamer == null)
                        continue;

                    Player player = gamer.getPlayer();
                    if (player == null || !player.isOnline())
                        continue;

                    players.remove(player);

                    if (!isSkyBlockWorld(player.getWorld()))
                        continue;

                    players.forEach(target -> borderAPI.sendBoard(target, middle, size));
                    borderAPI.sendBoard(player, middle, size);
                }

                if (size >= finalSize)
                    cancel();
            }

        }.runTaskTimerAsynchronously(javaPlugin, 1L, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncGamerJoinEvent e) {
        val gamer = e.getGamer();
        if (gamer == null)
            return;

        val player = gamer.getPlayer();
        val island = ISLAND_MANAGER.getIsland(player.getLocation());
        if (island == null)
            return;

        sendBoarder(player, island);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(UserTeleportByCommandEvent e) {
        if (e.isCancelled())
            return;

        Player player = e.getUser().getPlayer();
        if (player == null) {
            return;
        }

        Location to = e.getLocation();

        if (!isSkyBlockWorld(to.getWorld())) {
            borderAPI.removeBoard(player);
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Island island = ISLAND_MANAGER.getIsland(to);
        if (island == null)
            return;

        IgnoreModule ignoreModule = island.getModule(IgnoreModule.class);
        if (ignoreModule != null
                && ignoreModule.getIgnoreList().containsKey(gamer.getPlayerID())
                && !island.hasMember(gamer)) {
            e.setCancelled(true);
            gamer.sendMessageLocale("ISLAND_MEMBER_BLOCKED", island.getOwner().getDisplayName());
            return;
        }

        sendBoarder(player, island);
    }

    private void sendBoarder(Player player, Island island) {
        SkyGamer skyGamer = manager.getSkyGamer(player);
        if (skyGamer == null || !skyGamer.isBorder()) {
            return;
        }

        BorderModule module = island.getModule(BorderModule.class);
        if (module == null) {
            return;
        }

        int size = ((module.getSize() / 2) * 16 + 8) * 2;
        Location middle = island.getTerritory().getMiddleChunk().getMiddle();

        BukkitUtil.runTaskLaterAsync(10L, ()-> borderAPI.sendBoard(player, middle, size));
    }


    @EventHandler
    public void onGenerator(BlockFromToEvent e) { //генератор каблы
        Block from = e.getBlock();
        Block to = e.getToBlock();

        if (!isSkyBlockWorld(to.getWorld()))
            return;

        if (to.getType() != Material.AIR || from.getType() != Material.STATIONARY_LAVA || !isSurroundedByWater(to)) {
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(to.getLocation());
        if (island == null) {
            return;
        }

        GeneratorModule module = island.getModule(GeneratorModule.class);
        if (module == null)
            return;

        e.setCancelled(true);
        Material material = module.getActiveGenerator().getBlock();
        to.setType(material);

        if (material == Material.COBBLESTONE || material == Material.STONE) {
            return;
        }

        to.setMetadata("generator", new FixedMetadataValue(javaPlugin, to.getLocation()));
    }

    private static boolean isSurroundedByWater(Block to) {
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                Block waterBlock = to.getRelative(x, 0, z);
                if (waterBlock != null && waterBlock.getType() == Material.STATIONARY_WATER)
                    return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        block.removeMetadata("generator", javaPlugin);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e) {
        for (Block block : e.blockList()) {
            block.removeMetadata("generator", javaPlugin);
        }
    }

    @EventHandler
    public void onPistonRet(BlockPistonRetractEvent e) {
        for (Block block : e.getBlocks()) {
            if (!block.getMetadata("generator").isEmpty()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonExt(BlockPistonExtendEvent e) {
        for (Block block : e.getBlocks()) {
            if (!block.getMetadata("generator").isEmpty()) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
