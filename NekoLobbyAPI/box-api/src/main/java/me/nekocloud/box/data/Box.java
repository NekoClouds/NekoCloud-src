package me.nekocloud.box.data;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.depend.PacketObject;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.box.api.ItemBox;
import me.nekocloud.box.util.BoxUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Box {
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final HologramAPI HOLOGRAM_API = NekoCloud.getHologramAPI();
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private static final Random RANDOM = new Random();
    private static final ParticleAPI PARTICLE_API = NekoCloud.getParticleAPI();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private final boolean rotateByX = true;
    private static final List<Future<?>> THREADS = new ArrayList<>();
    private Future<?> future = null;

    @Getter
    private final Block block;
    private final Location location;
    private final TIntObjectMap<Hologram> holograms = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    @Getter
    private final Set<String> playersOpenGui = new ConcurrentSet<>();

    @Getter
    private Player owner; //кто крутит кейс
    @Getter
    private boolean work; //крутится ли он сейчас или нет

    public Box(Location location) {
        this.block = location.getBlock();
        this.location = block.getLocation().clone().add(0.5, 0.5, 0.5);
        this.location.setYaw(0.0f);
        this.location.setPitch(0.0f);

        for (val language : Language.values()) {
            val hologram = HOLOGRAM_API.createHologram(getLocation().add(0, 0.3, 0));
            hologram.addTextLine(getName(language));
            hologram.addTextLine(language.getMessage("BOX_SUB_NAME"));
            holograms.put(language.getId(), hologram);
        }
    }

    private String getName(Language lang) {
        return lang.getMessage("BOX_NAME");
    }

    public Location getLocation(){
        return location.clone();
    }

    public String getPrefix(Language lang) {
        return getName(lang) + " §8| §f";
    }

    public Hologram getHologram(Language lang) {
        Hologram hologram = holograms.get(lang.getId());
        if (hologram == null) {
            hologram = holograms.get(Language.DEFAULT.getId());
        }

        return hologram;
    }

    public void onStart(BukkitGamer gamer, List<ItemBox> items, ItemBox winItem, ItemStack chestItem) {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        for (String name : playersOpenGui) {
            Player other = Bukkit.getPlayer(name);
            if (other != null) {
                other.closeInventory();
            }
        }
        playersOpenGui.clear();

        this.owner = player;
        work = true;
        EXECUTOR_SERVICE.execute(() -> {
            try {
                BoxUtil.enableBoxHologram(this, false);

                holograms.valueCollection().forEach(PacketObject::hideAll);
                Thread.sleep(200);
                BoxUtil.playChestAnimation(block, 1);
                Thread.sleep(800);

                Location location = this.location.clone();
                BlockFace face = ((Directional)block.getState().getData()).getFacing();
                location.setYaw(BoxUtil.directionToYaw(face));
                location.setPitch(BoxUtil.directionToYaw(face));

                Hologram chestHologram = HOLOGRAM_API.createHologram(location);
                chestHologram.addBigItemLine(false, chestItem); //можно менять маленький сундук в зависимости от ключа
                chestHologram.setPublic(true);
                location.getWorld().playSound(location, SOUND_API.getSound(SoundType.FIREWORK_LAUNCH), 1.0F, 1.0F);
                Thread.sleep(200);
                double startR = 2.0, radiusRate = 0.007, yRate = 0.005;
                final int positions = 100, firstIterations = 20;
                int nowPosition = (positions / 2);
                List<Location> circlePositions = BoxUtil.getCircleSide(location, startR);
                org.bukkit.util.Vector dist = circlePositions.get(nowPosition).subtract(chestHologram.getLocation()).toVector();
                for (int i = 1; i <= firstIterations; ++i) {
                    Location current = location.clone().add(dist.clone().multiply(((double)i) / firstIterations));
                    current.setY(location.getY());
                    chestHologram.onTeleport(current);
                    Thread.sleep(15);
                }
                double nowY = location.getY();
                while (startR >= 0.05) {
                    circlePositions = BoxUtil.getCircleSide(location, startR);
                    Location now = circlePositions.get((nowPosition++) % positions);
                    now.setY(nowY);
                    chestHologram.onTeleport(now);
                    PARTICLE_API.sendEffect(ParticleEffect.FIREWORKS_SPARK, chestHologram.getLocation().clone()
                            .add(0, 0.8, 0), 0.01F, 1);
                    nowY += yRate;
                    startR -= radiusRate;
                    Thread.sleep(7);
                }

                Location center = location.clone();
                center.setY(nowY);
                chestHologram.onTeleport(center);
                Thread.sleep(600);
                Location winHoloLocation = chestHologram.getLocation();
                chestHologram.remove();
                PARTICLE_API.sendEffect(ParticleEffect.EXPLOSION_NORMAL, winHoloLocation.clone()
                        .add(0, 0.4, 0), 0.05F, 25);
                location.getWorld().playSound(location, SOUND_API.getSound(SoundType.FIREWORK_BLAST2), 1.0F, 1.0F);

                Collections.shuffle(items);
                final int timesScroll = RANDOM.nextInt(6) + 3;
                for (int i = 0; i < timesScroll; ++i) {
                    location.getWorld().playSound(location, SOUND_API.getSound(SoundType.CLICK), 1.0F, 1.0F);
                    Vector to = winHoloLocation.clone().subtract(location).toVector();

                    List<Hologram> itemHologramList = new ArrayList<>();
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        BukkitGamer plGamer = GAMER_MANAGER.getGamer(pl);

                        if (plGamer == null) {
                            continue;
                        }

                        Language langGamer = plGamer.getLanguage();

                        Hologram itemHolo = HOLOGRAM_API.createHologram(location.clone().add(0, 0.7, 0));
                        itemHolo.addTextLine(items.get(i % items.size()).getName(langGamer));
                        itemHolo.addDropLine(false, items.get(i % items.size()).getIcon());
                        itemHolo.showTo(pl);

                        itemHologramList.add(itemHolo);
                    }

                    for (int j = 0; j < 10; ++j) {
                        Location spawnLocation = location.clone().add(0, 0.7, 0)
                                .clone().add(to.clone().multiply((j + 1.0) / 10.0));
                        itemHologramList.forEach(hologram -> hologram.onTeleport(spawnLocation));
                        Thread.sleep(25);
                    }
                    Thread.sleep(250);
                    itemHologramList.forEach(PacketObject::remove);
                }

                List<Hologram> winHolo = new ArrayList<>();
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    BukkitGamer otherGamer = GAMER_MANAGER.getGamer(otherPlayer);

                    if (otherGamer == null)
                        continue;

                    Language otherLang = otherGamer.getLanguage();

                    Hologram holo = HOLOGRAM_API.createHologram(winHoloLocation.clone().add(0, 0.5, 0));
                    holo.addTextLine(winItem.getName(otherLang));
                    holo.addDropLine(false, winItem.getIcon());
                    holo.showTo(otherPlayer);
                    winHolo.add(holo);

                    if (otherPlayer.equals(player)){
                        if (!player.isOnline()) {
                            continue;
                        }
                        player.sendMessage(otherLang.getMessage( "BOX_WINNER",
                                winItem.getRarity().getName(otherLang), winItem.getName(otherLang)));
                        winItem.onMessage(gamer);
                        continue;
                    }

                    otherPlayer.sendMessage(otherLang.getMessage( "BOX_WINNER_OTHER",
                            player.getDisplayName(), winItem.getRarity().getName(otherLang),
                            winItem.getName(otherLang)));
                }

                val effect = winItem.getRarity().getEffect();
                if (effect != null) {
                    PARTICLE_API.sendEffect(effect, getLocation().add(0, 1.7, 0), 0.0F, 0.0F,
                            0.0F, 0.05F, 30, 128);
                }

                location.getWorld().playSound(location, SOUND_API.getSound(SoundType.LEVEL_UP), 1.0F, 1.0F);
                Thread.sleep(2000);
                winHolo.forEach(holo -> holo.onTeleport(location));
                Thread.sleep(5 * 50);
                winHolo.forEach(PacketObject::remove);
                BoxUtil.playChestAnimation(block, 0);
                Thread.sleep(600);

                this.owner = null;
                work = false;
                showHolograms();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void startRoundAnimation(BukkitGamer gamer, List<ItemBox> items, ItemBox winItem, ItemStack chestItem) {
        this.future = EXECUTOR_SERVICE.submit(() -> {
            try {
                double t = 0.0;
                int time = 0;
                int endTime = -1;
                int offset = 0;
                int particlesOffset = 0;
                int tick = 0;

                boolean cancelRotate = false;
                boolean frostLordEnable = true;

                Location hologramSpawn = location.clone().subtract(0.0, 1.0, 0.0);
                Location frostLord = location.clone().subtract(0.0, 1.0, 0.0);
                Location heart = location.clone().add(0.0, 1.75, 0.0);
                Location winLocation = hologramSpawn.clone().add(0.0, 1.75, 0.0);

                ArrayList<Hologram> holograms = new ArrayList<>();
                Hologram winHologram = null;

                while (frostLordEnable) {
                    Thread.sleep(50L);
                    t += 0.19634954084936207;
                    for (double pi = 0.0; pi <= Math.PI * 2; pi += 1.5707963267948966) {
                        double x = 0.2 * (Math.PI * 4 - t) * Math.cos(t + pi);
                        double y = 0.2 * t;
                        double z = 0.2 * (Math.PI * 4 - t) * Math.sin(t + pi);
                        frostLord.add(x, y, z);
                        PARTICLE_API.sendEffect(ParticleEffect.FIREWORKS_SPARK, frostLord);
                        frostLord.subtract(x, y, z);

                        if (!(t >= Math.PI * 4)) continue;

                        frostLordEnable = false;

                        BoxUtil.playChestAnimation(this.block, 1);
                    }
                }

                while (work) {
                    Thread.sleep(50L);
                    if (tick == 5) {
                        PARTICLE_API.sendEffect(ParticleEffect.HEART, heart);
                        if (!cancelRotate) {
                            SOUND_API.play(location, SoundType.CLICK);
                        }
                        tick = 0;
                    }
                    if (endTime == -2) {
                        endTime = time + 1;
                    }

                    int index = 0;

                    double c1 = Math.toRadians((double)particlesOffset % 360.0);

                    for (double d = 0.0; d < Math.PI * 4; d += 0.7853981633974483) {
                        if (index >= 4) {
                            index = 0;
                        }

                        double a = d + c1;
                        double cos = Math.cos(a);
                        double sin = Math.sin(a);

                        Color color = Color.RED;
                        if (index == 1) {
                            color = Color.YELLOW;
                        }
                        if (index == 2) {
                            color = Color.fromRGB((int)0, (int)253, (int)0);
                        }
                        if (index == 3) {
                            color = Color.AQUA;
                        }

                        Location particle = this.location.clone();

                        if (rotateByX) {
                            particle.add(cos * 1.5, sin * 1.5, 0.0);
                        } else {
                            particle.add(0.0, sin * 1.5, cos * 1.5);
                        }

                        PARTICLE_API.sendEffect(ParticleEffect.REDSTONE, color.getRed(), color.getGreen(), color.getBlue(), particle, 30.0);
                        ++index;
                    }
                    particlesOffset += 5;
                    if (!cancelRotate && holograms.size() < items.size() && Math.toRadians(offset) >= Math.PI * 2 / (double)items.size() * (double)holograms.size()) {
                        val as2 = HOLOGRAM_API.createHologram(hologramSpawn);

                        as2.addTextLine((items.get(holograms.size())).getName(Language.DEFAULT));
                        as2.addDropLine(false, items.get(holograms.size()).getIcon());
                        as2.setPublic(true);
                        if ((items.get(holograms.size())).equals(winItem)) {
                            winHologram = as2;
                        }
                        holograms.add(as2);
                        continue;
                    }
                    if (!cancelRotate) {
                        int asNum = 0;
                        double cO = Math.toRadians((double)offset % 360.0);
                        for (double d2 = 0.0; d2 < Math.PI * 2 && asNum < holograms.size(); d2 += Math.PI * 2 / (double)items.size()) {
                            double a2 = d2 + cO;
                            double cos2 = Math.cos(a2);
                            double sin2 = Math.sin(a2);
                            Hologram as3 = holograms.get(asNum++);
                            Location teleport = hologramSpawn.clone();
                            if (rotateByX) {
                                teleport.add(cos2 * 1.5, sin2 * 1.5 + 0.75, 0.0);
                            } else {
                                teleport.add(0.0, sin2 * 1.5 + 0.75, cos2 * 1.5);
                            }
                            as3.onTeleport(teleport);
                        }
                        double m = 1.0;
                        if (holograms.size() == items.size()) {

                            if (endTime == -1) {
                                endTime = time + 100;
                            }

                            if (time >= endTime) {
                                if (winHologram.getLocation().distance(winLocation) <= 0.05) {
                                    cancelRotate = true;
                                    this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                    BukkitUtil.runTask(() -> {
                                        winItem.onApply(gamer);
                                        winItem.onMessage(gamer);
                                    });
                                    BukkitUtil.runTaskLaterAsync(60L, () -> {
                                        work = false;
                                    });
                                }
                                endTime = time + 1;
                            }
                            int reCasesing = endTime - time;
                            m = 0.2 + (double)reCasesing / 20.0 / 10.0;
                        }
                        offset = (int)((double)offset + 10.0 * m);
                        ++time;
                    }
                    ++tick;
                }

                holograms.forEach(PacketObject::remove);
                holograms.clear();

                BoxUtil.playChestAnimation(block, 1);

                showHolograms();

                THREADS.remove(future);
                work = false;
            }
            catch (InterruptedException ignored) {
                BoxUtil.playChestAnimation(block, 0);
            }
        });

        THREADS.add(future);
    }


    private void showHolograms() {
        for (val gamer : GAMER_MANAGER.getGamers().values()) {
            val lang = gamer.getLanguage();
            getHologram(lang).showTo(gamer);
        }

        BoxUtil.enableBoxHologram(this, true);
    }


    public void remove() {
        holograms.valueCollection().forEach(PacketObject::remove);
    }
}
