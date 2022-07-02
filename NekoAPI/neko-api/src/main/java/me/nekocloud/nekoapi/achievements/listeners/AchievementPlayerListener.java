package me.nekocloud.nekoapi.achievements.listeners;

import com.comphenix.protocol.wrappers.MinecraftKey;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.achievements.achievement.Achievement;
import me.nekocloud.nekoapi.achievements.achievement.AchievementPlayer;
import me.nekocloud.nekoapi.achievements.manager.AchievementManager;
import me.nekocloud.nekoapi.achievements.manager.AchievementPlayerManager;
import me.nekocloud.nekoapi.listeners.DListener;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class AchievementPlayerListener extends DListener<JavaPlugin> {

    /**
     * регаем если нужно(на сурвачах например)
     */

    private final AchievementPlayerManager playerManager;
    private final AchievementManager achievementManager;

    public AchievementPlayerListener(AchievementPlayerManager playerManager,
                                     AchievementManager achievementManager,
                                     JavaPlugin javaPlugin) {
        super(javaPlugin);

        this.playerManager = playerManager;
        this.achievementManager = achievementManager;

        /*
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(javaPlugin,
                PacketType.Play.Client.ADVANCEMENTS) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer handle = event.getPacket();
                Status status = handle.getEnumModifier(Status.class, 0).readSafely(0);
                if (status == Status.CLOSED_SCREEN) {
                    return;
                }

                Player player = event.getPlayer();
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null) {
                    return;
                }

                AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getOrCreateAchievementPlayer(gamer, achievementManager);
                if (achievementPlayer == null) {
                    return;
                }

                Language lang = gamer.getLanguage();
                Map<MinecraftKey, AdvancementProgress> progressMap = new HashMap<>();

                List<Advancement> advancements = new ArrayList<>();
                int count = 0;
                for (Achievement achievement : achievementManager.getAchievements().values()) {
                    Advancement parent = count > 0 ? advancements.getById(count - 1) : null;

                    advancements.add(getAdvancement(progressMap, parent, achievement,
                            lang, ++count, achievementPlayer.hasAchievement(achievement)));
                }

                PacketPlayOutAdvancements packet = new PacketPlayOutAdvancements(true, advancements,
                        new HashSet<>(), progressMap);

                event.setCancelled(true);

                BukkitUtil.runTaskLater(5L, ()-> NmsAPI.getManager().sendPacket(player, packet));
            }
        });
        */
    }

    private Advancement getAdvancement(Map<MinecraftKey, AdvancementProgress> progressMap,
                                       Advancement parent, Achievement achievement, Language language,
                                       int count, boolean complete) {

//        AdvancementDisplay advancementDisplay = new AdvancementDisplay(
//                CraftItemStack.asNMSCopy(complete ? achievement.getItem() : new ItemStack(Material.BARRIER)),
//                new ChatMessage((complete ? "§a" : "§c") + achievement.getName(language)),
//                new ChatMessage(""), //todo нормальное описание
//                new MinecraftKey(achievementManager.getTexture()),
//                complete ? AdvancementFrameType.CHALLENGE : AdvancementFrameType.TASK,
//                false, false, false
//        );
//
//        advancementDisplay.a(count, 0); //позиция //todo нормально высчитывать позицию
//
//        Map<String, Criterion> map = new HashMap<>();
//        map.put("completed", new Criterion(() -> null));
//        AdvancementProgress advancementProgress = new AdvancementProgress();
//        String[][] array2D = new String[][] {new String[] {"completed"}};
//        advancementProgress.a(map, array2D);
//        MinecraftKey minecraftKey = new MinecraftKey("LastCraft", "LastCraft/" + achievement.getId());
//
//        if (complete) {
//            advancementProgress.getCriterionProgress("completed").b();
//            progressMap.put(minecraftKey, advancementProgress);
//        }
//
//        return new Advancement(minecraftKey, parent, advancementDisplay, null, map, array2D);
        return null;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (achievementManager.isLoadOnJoin()) {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(e.getName());
            if (gamer == null) {
                return;
            }

            playerManager.addAchievementPlayer(new AchievementPlayer(gamer, achievementManager));
        }
    }

    @EventHandler
    public void onQuit(AsyncGamerQuitEvent e) {
        String name = e.getGamer().getName();
        AchievementPlayer achievementPlayer = playerManager.getAchievementPlayer(name);
        if (achievementPlayer == null) {
            return;
        }

        achievementPlayer.save();

        playerManager.removeAchievementPlayer(name);
    }

    public enum Status {
        OPENED_TAB,
        CLOSED_SCREEN;
    }
}
