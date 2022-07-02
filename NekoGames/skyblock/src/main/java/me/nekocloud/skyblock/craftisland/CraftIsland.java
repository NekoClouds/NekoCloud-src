package me.nekocloud.skyblock.craftisland;

import me.nekocloud.skyblock.api.manager.EntityManager;
import me.nekocloud.skyblock.api.manager.IslandManager;
import me.nekocloud.skyblock.api.manager.TerritoryManager;
import me.nekocloud.skyblock.api.territory.IslandTerritory;
import me.nekocloud.skyblock.manager.CraftIslandManager;
import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.TitleAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.entity.IslandEntity;
import me.nekocloud.skyblock.api.event.IslandAsyncResetEvent;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.IslandModule;
import me.nekocloud.skyblock.api.island.IslandType;
import me.nekocloud.skyblock.api.island.member.IslandMember;
import me.nekocloud.skyblock.api.island.member.MemberType;
import me.nekocloud.skyblock.module.BorderModule;
import me.nekocloud.skyblock.module.ModuleData;
import me.nekocloud.skyblock.utils.FaweUtils;
import me.nekocloud.skyblock.utils.IslandLoader;
import me.nekocloud.skyblock.utils.ItemsContainer;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CraftIsland implements Island {
    private static final TitleAPI TITLE_API = NekoCloud.getTitlesAPI();
    private static final IslandManager MANAGER = SkyBlockAPI.getIslandManager();
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private static final TerritoryManager TERRITORY_MANAGER = SkyBlockAPI.getTerritoryManager();
    private static final EntityManager ENTITY_MANAGER = SkyBlockAPI.getEntityManager();
    private static final UserManager USER_MANAGER = CommonsSurvivalAPI.getUserManager();

    private IBaseGamer owner;
    private int islandID;
    private int money;
    private Biome biome;
    private IslandType islandType;
    private int level;

    private final IslandTerritory territory;
    private final Map<Integer, IslandMember> members = new ConcurrentHashMap<>();
    private final Map<String, IslandModule> modules = new ConcurrentHashMap<>();

    //новому игроку создаем
    public CraftIsland(IBaseGamer owner, IslandType islandType) {
        int playerID = owner.getPlayerID();

        this.owner = owner;
        this.money = 0;
        this.level = 0;
        this.islandType = islandType;
        members.put(playerID, new CraftIslandMember(this, playerID,
                MemberType.OWNER, System.currentTimeMillis()));
        this.territory = TERRITORY_MANAGER.get();

        this.islandID = IslandLoader.addIsland(territory.getCord().getFirst(),
                territory.getCord().getSecond(),
                playerID, islandType);

        //регаем модули и подгружаем базовую инфу
        Arrays.stream(ModuleData.values()).forEach(moduleData -> {
            addModule(moduleData.getModuleClass());
            getModule(moduleData.getModuleClass()).preLoad();
        });
    }

    //старому(из базы при старте сервера)
    public CraftIsland(int islandID, int i, int j, int playerID, int money, int level, IslandType islandType, long date) {
        this.islandID = islandID;
        this.money = money;
        this.level = level;
        this.owner = GAMER_MANAGER.getOrCreate(playerID);
        this.islandType = islandType;
        members.put(playerID, new CraftIslandMember(this, playerID, MemberType.OWNER, date));
        this.territory = TERRITORY_MANAGER.getTerritory(i, j);

        IslandLoader.loadMembers(this, members);

        //регаем модули
        Arrays.stream(ModuleData.values()).forEach(moduleData -> addModule(moduleData.getModuleClass()));
    }

    @Override
    public int getIslandID() {
        return islandID;
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return (hasMember(player) && getModule(BorderModule.class).canBuild(location));
    }

    @Override
    public boolean containsLocation(Location location) {
        return (territory.canInteract(location) && getModule(BorderModule.class).canBuild(location));
    }

    @Override
    public boolean hasMember(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        return gamer != null && hasMember(gamer);
    }

    @Override
    public boolean hasMember(IBaseGamer gamer) {
        if (gamer == null)
            return false;
        IslandMember islandMember = members.get(gamer.getPlayerID());
        return islandMember != null;
    }

    @Override
    public boolean hasMember(IslandMember member) {
        return members.containsKey(member.getPlayerID());
    }

    @Override
    public IBaseGamer getOwner() {
        BukkitGamer bukkitGamer = GAMER_MANAGER.getGamer(owner.getPlayerID());
        if (bukkitGamer != null)
            this.owner = bukkitGamer;

        return owner;
    }

    @Override
    public boolean hasOwner(IBaseGamer gamer) {
        return gamer != null && hasOwner(gamer.getPlayerID());
    }

    @Override
    public boolean hasOwner(int playerID) {
        return owner.getPlayerID() == playerID;
    }

    @Override
    public boolean hasOwner(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        return gamer != null && hasOwner(gamer);
    }

    @Override
    public IslandTerritory getTerritory() {
        return territory;
    }

    @Override
    public <T extends IslandModule> T getModule(Class<T> clazz) {
        return (T) modules.get(clazz.getSimpleName());
    }

    @Override
    public List<IslandEntity> getEntities() {
        return ENTITY_MANAGER.getEntities(territory);
    }

    @Override
    public List<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();
        for (IslandMember islandMember : members.values()) {
            IBaseGamer gamer = islandMember.getGamer();
            if (!gamer.isOnline()) {
                continue;
            }

            BukkitGamer bukkitGamer = (BukkitGamer) gamer;

            Player player = bukkitGamer.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }

            players.add(player);
        }

        return players;
    }

    @Override
    public List<BukkitGamer> getOnlineGamers() {
        List<BukkitGamer> gamers = new ArrayList<>();
        for (IslandMember islandMember : members.values()) {
            IBaseGamer gamer = islandMember.getGamer();
            if (!gamer.isOnline())
                continue;

            gamers.add((BukkitGamer) gamer);
        }

        return gamers;
    }

    @Override
    public int getLevel() {
        return level; //todo сделать повышение лвла
    }

    @Override
    public List<IslandMember> getMembers(MemberType type) {
        return members.values().stream()
                .filter(islandMember -> islandMember.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<IslandMember> getMembers() {
        return new ArrayList<>(members.values());
    }

    @Override
    public List<IBaseGamer> getGamerMembers() {
        List<IBaseGamer> gamers = new ArrayList<>();
        members.values().forEach((islandMember) -> {
            IBaseGamer gamer = islandMember.getGamer();
            gamers.add(gamer);
        });
        return gamers;
    }

    @Override
    public IslandType getIslandType() {
        return islandType;
    }

    @Override
    public void addPlayerToIsland(Player player, MemberType memberType) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        int playerID = gamer.getPlayerID();
        members.put(playerID, new CraftIslandMember(this, playerID, MemberType.MEMBER, System.currentTimeMillis()));

        ((CraftIslandManager)MANAGER).addMember(this, playerID, true);
    }

    @Override
    public void removePlayerFromIsland(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        removePlayerFromIsland(gamer.getPlayerID());
    }

    @Override
    public void removePlayerFromIsland(int playerID) {
        MemberType memberType = getMemberType(playerID);
        if (memberType == MemberType.NOBODY)
            return;

        members.remove(playerID);

        ((CraftIslandManager)MANAGER).removeMember(this, playerID);
    }

    @Override
    public void changeMemberType(IslandMember islandMember, MemberType newMemberType) {
        int playerID = islandMember.getPlayerID();
        if (!members.containsKey(playerID))
            return;

        IslandLoader.changeMemberType(this, playerID, newMemberType);
    }

    @Override
    public void broadcastMessageLocale(String key, Object... objects) {
        getOnlineGamers().forEach(gamer -> gamer.sendMessageLocale(key, objects));
    }

    @Override
    public MemberType getMemberType(int playerID) {
        if (owner.getPlayerID() == playerID)
            return MemberType.OWNER;

        IslandMember islandMember = members.get(playerID);
        if (islandMember != null)
            return islandMember.getType();

        return MemberType.NOBODY;
    }

    @Override
    public MemberType getMemberType(IBaseGamer gamer) {
        return getMemberType(gamer.getPlayerID());
    }

    @Override
    public Biome getBiome() {
        if (biome == null) {
            Location location = territory.getMiddleChunk().getMiddle();
            this.biome = FaweUtils.getBiome(location);
        }
        return biome;
    }

    @Override
    public void setBiome(Biome biome) {
        this.biome = biome;
        FaweUtils.setBiome(territory, biome);
    }

    @Override
    public void resetBiome() {
        setBiome(Biome.FOREST);
    }

    @Override
    public int getMoney() {
        return money;
    }

    @Override
    public boolean changeMoney(int money) {
        if (this.money + money < 0) {
            return false;
        } else {
            this.money += money;
            IslandLoader.changeMoney(this, money);
            return true;
        }
    }

    @Override
    public int getLimitMembers() {
        IBaseGamer owner = getOwner();
        if (owner.isDefault())
            return 10;

        if (owner.isAxside())
            return 50;

        if (owner.isTrival())
            return 40;

        if (owner.isAkio())
            return 30;

        return 20;
    }

    @Override
    public <T extends IslandModule> void addModule(Class<T> clazz) {
        try {
            IslandModule module = clazz.getConstructor(Island.class)
                    .newInstance(this);
            modules.put(module.getName(), module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, IslandModule> getModules() {
        return modules;
    }

    @Override
    public void reset(IslandType islandType) {
        this.islandType = islandType;
        IslandAsyncResetEvent event = new IslandAsyncResetEvent(this);
        BukkitUtil.runTaskAsync(() -> {
            BukkitUtil.callEvent(event);

            if (event.isCancelled())
                return;

            BukkitGamer gamer = GAMER_MANAGER.getGamer(owner.getPlayerID());
            if (gamer == null) {
                return;
            }

            User user = USER_MANAGER.getUser(gamer.getName());
            if (user == null) {
                return;
            }

            Player player = gamer.getPlayer();

            Language lang = gamer.getLanguage();
            Location locationHome = territory.getMiddleChunk().getMiddle();

            Group group = gamer.getGroup();
            String schematicName = islandType.getNameFile();
            List<ItemStack> items = ItemsContainer.getItems(group).getItems();

            BukkitUtil.runTask(() -> player.teleport(CommonsSurvivalAPI.getSpawn()));

            try {
                FaweUtils.resetBlocks(territory);
                FaweUtils.pasteSchematic(locationHome, schematicName, items);
                Biome biome = islandType.getBiome();
                FaweUtils.setBiome(territory, biome);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BukkitUtil.runTaskLater(20L, () -> {
                if (user.teleport(locationHome)) {
                    getModules().values().forEach(IslandModule::resetIsland);

                    locationHome.getWorld().spawnEntity(locationHome, EntityType.COW);

                    TITLE_API.sendTitle(player,
                            lang.getMessage("ISLAND_CREATE_TITLE"),
                            lang.getMessage("ISLAND_CREATE_SUBTITLE"));
                }
            });
        });
    }

    @Override
    public void delete() {
        MANAGER.delete(this);
    }
}
