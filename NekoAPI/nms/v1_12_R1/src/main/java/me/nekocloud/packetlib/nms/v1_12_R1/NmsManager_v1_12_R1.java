package me.nekocloud.packetlib.nms.v1_12_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import me.nekocloud.packetlib.nms.interfaces.DWorldBorder;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import me.nekocloud.packetlib.nms.interfaces.entity.*;
import me.nekocloud.packetlib.nms.interfaces.exeption.ClassNotPacketException;
import me.nekocloud.packetlib.nms.interfaces.gui.DAnvil;
import me.nekocloud.packetlib.nms.interfaces.gui.DEnchantingTable;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.util.ReflectionUtils;
import me.nekocloud.packetlib.nms.v1_12_R1.entity.*;
import me.nekocloud.packetlib.nms.v1_12_R1.gui.DAnvilImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.gui.DEnchantingTableImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.PacketContainerImpl;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.*;

public class NmsManager_v1_12_R1 implements NmsManager {

    private final PacketContainer container = new PacketContainerImpl();

    @Override
    public <T extends DEntity> T createDEntity(Class<T> classEntity, Location location) {
        if (classEntity == DEntity.class || classEntity == DEntityLiving.class || classEntity == DEntityPlayer.class)
            throw new IllegalArgumentException("Вы не можете создать таких энтите");
        net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        DEntity entity = null;
        if (DEntityLiving.class.isAssignableFrom(classEntity)) {
            if (DEntityArmorStand.class.isAssignableFrom(classEntity)) {
                entity = new DEntityArmorStandImpl(nmsWorld);
            } else if (DEntityBlaze.class.isAssignableFrom(classEntity)) {
                entity = new DEntityBlazeImpl(nmsWorld);
            } else if (DEntityCow.class.isAssignableFrom(classEntity)) {
                entity = new DEntityCowImpl(nmsWorld);
            } else if (DEntityCreeper.class.isAssignableFrom(classEntity)) {
                entity = new DEntityCreeperImpl(nmsWorld);
            } else if (DEntityDragon.class.isAssignableFrom(classEntity)) {
                entity = new DEntityDragonImpl(nmsWorld);
            } else if (DEntityMushroomCow.class.isAssignableFrom(classEntity)) {
                entity = new DEntityMushroomCowImpl(nmsWorld);
            } else if (DEntitySlime.class.isAssignableFrom(classEntity)) {
                entity = new DEntitySlimeImpl(nmsWorld);
            } else if (DEntityVillager.class.isAssignableFrom(classEntity)) {
                entity = new DEntityVillagerImpl(nmsWorld);
            } else if (DEntityWolf.class.isAssignableFrom(classEntity)) {
                entity = new DEntityWolfImpl(nmsWorld);
            } else if (DEntityZombie.class.isAssignableFrom(classEntity)) {
                entity = new DEntityZombieImpl(nmsWorld);
            } else if (DEntityGiantZombie.class.isAssignableFrom(classEntity)) {
                entity = new DEntityGiantZombieImpl(nmsWorld);
            }
        } else if (DEntityItem.class.isAssignableFrom(classEntity)) {
            entity = new DItemImpl(nmsWorld);
        }

        if (entity == null)
            throw new NullPointerException("Кажется что-то пошло не так и админ обосрался!");

        entity.setLocation(location);
        return (T) entity;
    }

    @Override
    public DWorldBorder createBorder(World world) {
        return new DWorldBorderImpl(world);
    }

    @Override
    public DEntityPlayer createEntityPlayer(World world, GameProfile gameProfile) {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        return new DEntityPlayerImpl(minecraftServer, worldServer, gameProfile,
                new PlayerInteractManager(worldServer));
    }

    @Override
    public void updateScaledHealth(Player player) {
        ((CraftPlayer) player).updateScaledHealth();
    }

    @Override
    public void triggerHealthUpdate(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.triggerHealthUpdate();
    }

    @Override
    public void updateAbilities(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.updateAbilities();
    }

    @Override
    public int getEntityID(Entity entity) {
        return ((CraftEntity) entity).getHandle().getId();
    }

    @Override
    public GameProfile getGameProfile(Player player) {
        if (player == null)
            return null;
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return entityPlayer.getProfile();
    }

    @Override
    public int getItemID(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItemStack == null)
            return 0;
        return Item.getId(nmsItemStack.getItem());
    }

    @Override
    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    @Override
    public void sendCrashClientPacket(Player target) {
        if (target == null || !target.isOnline())
            return;

        PacketPlayOutExplosion packet = new PacketPlayOutExplosion(Double.MAX_VALUE, Double.MAX_VALUE,
                Double.MAX_VALUE, Float.MAX_VALUE, Collections.EMPTY_LIST, new Vec3D(Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE));
        ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public DAnvil getAnvil(Player player) {
        return new DAnvilImpl(((CraftPlayer) player).getHandle());
    }

    @Override
    public Channel getChannel(Player player) {
        if (player == null || !player.isOnline())
            return null;

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        if (playerConnection == null)
            return null;

        return playerConnection.networkManager.channel;
    }

    @Override
    public DEnchantingTable getEnchantingTable(Player player) {
        return new DEnchantingTableImpl(player);
    }

    @Override
    public void setSkin(Player player, String value, String signature) {
        if (value == null || signature == null)
            return;

        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer nmsPlayer = craftPlayer.getHandle();

        if (player.isSleeping()) {
            PacketPlayInEntityAction action = new PacketPlayInEntityAction();

            ReflectionUtils.setFieldValue(action, "a", nmsPlayer.getId());
            ReflectionUtils.setFieldValue(action, "animation", PacketPlayInEntityAction.EnumPlayerAction.STOP_SLEEPING);

            nmsPlayer.playerConnection.networkManager.channel.pipeline().fireChannelRead(action);
        }

        GameProfile profile = nmsPlayer.getProfile();
        profile.getProperties().get("textures").clear();
        profile.getProperties().get("textures").add(new Property("textures", value, signature));

        org.bukkit.inventory.PlayerInventory inventory = player.getInventory();

        PacketPlayOutPlayerInfo removeTab = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo
                .EnumPlayerInfoAction.REMOVE_PLAYER, nmsPlayer);
        PacketPlayOutPlayerInfo addTab = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo
                .EnumPlayerInfoAction.ADD_PLAYER, nmsPlayer);

        int dimensionId = player.getWorld().getEnvironment().getId();
        WorldServer worldServer = (WorldServer) nmsPlayer.getWorld();
        EnumGamemode enumGamemode = EnumGamemode.valueOf(player.getGameMode().toString());
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dimensionId, worldServer.getDifficulty(),
                worldServer.worldData.getType(), enumGamemode);

        Location loc = player.getLocation();
        PacketPlayOutPosition pos = new PacketPlayOutPosition(loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch(),
                new HashSet<>(), -1337);

        PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;
        playerConnection.sendPacket(removeTab);
        playerConnection.sendPacket(addTab);
        playerConnection.sendPacket(respawn);
        playerConnection.sendPacket(pos);

        player.setExp(player.getExp());
        player.setWalkSpeed(player.getWalkSpeed());
        player.updateInventory();
        inventory.setHeldItemSlot(inventory.getHeldItemSlot());
        craftPlayer.updateScaledHealth();

        Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer.canSee(player))
                .forEach((other) -> {
                    other.hidePlayer(player);
                    other.showPlayer(player);
                });
    }

    @Override
    public void sendPacket(Player player, Object packet) {
        if (!(packet instanceof Packet))
            throw new ClassNotPacketException();

        if (player == null || !player.isOnline())
            return;

        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        if (handle == null) {
            return;
        }

        PlayerConnection playerConnection = handle.playerConnection;
        if (playerConnection == null) {
            return;
        }

        playerConnection.sendPacket((Packet<?>) packet);
    }

    @Override
    public void disableFire(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (entityPlayer.fireTicks <= 0)
            return;

        entityPlayer.fireTicks = 0;
    }

    @Override
    public PacketContainer getPacketContainer() {
        return container;
    }

    @Override
    public List<Block> getBlocksBelow(Player player) {
        ArrayList<Block> blocksBelow = new ArrayList<>();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        AxisAlignedBB boundingBox = entityPlayer.getBoundingBox();
        World world = player.getWorld();
        double yBelow = player.getLocation().getY() - 0.0001;
        Block northEast = new Location(world, boundingBox.d, yBelow, boundingBox.c).getBlock();
        Block northWest = new Location(world, boundingBox.a, yBelow, boundingBox.c).getBlock();
        Block southEast = new Location(world, boundingBox.d, yBelow, boundingBox.f).getBlock();
        Block southWest = new Location(world, boundingBox.a, yBelow, boundingBox.f).getBlock();
        Block[] blocks = {northEast, northWest, southEast, southWest};
        for (Block block : blocks) {
            if (!blocksBelow.isEmpty()) {
                boolean duplicateExists = false;
                for (Block aBlocksBelow : blocksBelow) {
                    if (aBlocksBelow.equals(block)) {
                        duplicateExists = true;
                    }
                }
                if (!duplicateExists) {
                    blocksBelow.add(block);
                }
            } else {
                blocksBelow.add(block);
            }
        }
        return blocksBelow;
    }

    @Override
    public void playChestAnimation(Block chest, boolean open) {
        Location loc = chest.getLocation();
        WorldServer worldServer = ((CraftWorld) loc.getWorld()).getHandle();
        worldServer.playBlockAction(new BlockPosition(
                        chest.getX(),
                        chest.getY(),
                        chest.getZ()),
                CraftMagicNumbers.getBlock(chest), 1, open ? 1 : 0);
    }

    @Override
    public void removeArrowFromPlayer(Player player) {
        if (player == null)
            return;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        DataWatcher watcher = entityPlayer.getDataWatcher();
        watcher.set(new DataWatcherObject<>(10, DataWatcherRegistry.b), 0);
    }

    @Override
    public void stopReadingPacket(Player player) {
        if (player == null)
            return;

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        if (connection == null)
            return;

        NetworkManager manager = connection.networkManager;
        if (manager == null)
            return;

        sendCrashClientPacket(player);
        manager.stopReading();
    }

    @Override
    public void sendMetaData(Player player, Entity entity) {
        if (entity == null)
            return;

        net.minecraft.server.v1_12_R1.Entity handle = ((CraftEntity) entity).getHandle();
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entity.getEntityId(), handle.getDataWatcher(), true);
        sendPacket(player, metadata);
    }

    @Override
    public void sendGlowing(Player player, Entity who, boolean glow) {
        if (player == null || who == null) {
            return;
        }

        net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) who).getHandle();

        DataWatcher toCloneDataWatcher = nmsEntity.getDataWatcher();
        DataWatcher newDataWatcher = new DataWatcher(nmsEntity);

        Map<Integer, DataWatcher.Item<?>> currentMap = ReflectionUtils.getFieldValue(toCloneDataWatcher, "d");
        Map<Integer, DataWatcher.Item<?>> newMap = new HashMap<>();

        for (Integer integer : currentMap.keySet()) {
            newMap.put(integer, currentMap.get(integer).d());//дублируем содержимое датавотчера
        }

        DataWatcher.Item item = newMap.get(0);
        byte initialBitMask = (Byte) item.b();
        byte bitMaskIndex = (byte) 6; //индекс этой херни с wiki.vg
        item.a((byte) (glow ? (initialBitMask | 1 << bitMaskIndex) : (initialBitMask & ~(1 << bitMaskIndex))));

        ReflectionUtils.setFieldValue(newDataWatcher, "d", newMap);

        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(who.getEntityId(),
                newDataWatcher, true);

        sendPacket(player, metadataPacket);
    }

    @Override
    public void launchInstantFirework(Firework firework) {
        EntityFireworks entityFireworks = ((CraftFirework) firework).getHandle();
        int expectedLifespan = entityFireworks.expectedLifespan;
        try {
            Field ticksFlown = EntityFireworks.class.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFireworks, expectedLifespan - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
