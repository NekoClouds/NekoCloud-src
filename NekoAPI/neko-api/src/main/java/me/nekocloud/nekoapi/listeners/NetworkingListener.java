package me.nekocloud.nekoapi.listeners;

import lombok.val;
import me.nekocloud.api.JSONMessageAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.event.gamer.GamerChangeGroupEvent;
import me.nekocloud.api.event.gamer.GamerFriendEvent;
import me.nekocloud.api.event.gamer.GamerLvlUpEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.sections.FriendsSection;
import me.nekocloud.base.util.JsonBuilder;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.core.connector.bukkit.event.BukkitGroupEvent;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public final class NetworkingListener extends DListener<NekoAPI> {

    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final ParticleAPI PARTICLE_API = NekoCloud.getParticleAPI();
    private static final JSONMessageAPI JSON_MESSAGE_API = NekoCloud.getJsonMessageAPI();
    private static final ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();

    public NetworkingListener(final NekoAPI nekoAPI) {
        super(nekoAPI);
    }

//    @EventHandler
//    public void onCoreSound(final CoreInputPacketEvent event) {
//        if(event.getPacket() instanceof BukkitPlaySound packet) {
//            SoundType sound = packet.getSoundType();
//
//            if (!NekoCloud.isGame()) {
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    SOUND_API.play(player, sound, packet.getVolume(), packet.getPitch());
//                }
//                return;
//            }
//
//            BukkitGamer gamer = GAMER_MANAGER.getGamer(packet.getPlayerID());
//            if (gamer == null) {
//                return;
//            }
//
//            SOUND_API.play(gamer.getPlayer(), sound, packet.getVolume(), packet.getPitch());
//        }
//    }

    @EventHandler(ignoreCancelled = true)
    public void onLvlUp(final @NotNull GamerLvlUpEvent e) {
        val level = e.getLevel();
        val expNextLevel = e.getExpNextLevel();

        val gamer = e.getGamer();

        sendEffect(gamer);

        val lang = gamer.getLanguage();
        gamer.sendMessage("");
        gamer.sendMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        gamer.sendMessage(StringUtil.stringToCenter(lang.getMessage("LVL_UP")));
        gamer.sendMessage("");
        gamer.sendMessage(StringUtil.stringToCenter(lang.getMessage("NEW_LVL",
                StringUtil.getNumberFormat(level))));
        gamer.sendMessage(StringUtil.stringToCenter(lang.getMessage("EXP_TO_NEW_LVL",
                StringUtil.getNumberFormat(expNextLevel))));
        gamer.sendMessage("");
        if (!(NekoCloud.isHub() || NekoCloud.isLobby())) {
            gamer.sendMessage(lang.getMessage("GET_REWARD"));
        } else {
            JSON_MESSAGE_API.send(gamer.getPlayer(), StringUtil.stringToCenter(new JsonBuilder()
                    .addText(lang.getMessage("GET_REWARD"))
                    .addRunCommand("§d", "/lvlreward", "GET_REWARD_HOVER")
                    .toString()));
        }
        gamer.sendMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        gamer.sendMessage("");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangePlayerFriends(final @NotNull GamerFriendEvent e) {//добавить/удалить новых друзей в мапу
        val gamer = e.getGamer();

        val section = gamer.getSection(FriendsSection.class);
        if (section != null) {
            section.changeFriend(e.getAction(), e.getFriend());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeCoreGroup(final @NotNull BukkitGroupEvent e) {
        val gamer = GAMER_MANAGER.getGamer(e.getPlayerID());
        if (gamer == null)
            return;

        val group = Group.getGroupByLevel(e.getGroupLevel());

        BukkitUtil.runTask(() -> BukkitUtil.callEvent(new GamerChangeGroupEvent(
                gamer,
                group
        )));
        // Обновляем в борде(хз почему этого не было на хепликсе)
        SCORE_BOARD_API.setPrefix(gamer.getPlayer(), group.getPrefix());
    }

    private void sendEffect(final @NotNull BukkitGamer gamer) {
        gamer.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE);
        val player = gamer.getPlayer();
        if (player == null) {
            return;
        }

        PARTICLE_API.launchInstantFirework(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.WHITE, Color.PURPLE)
                .build(), player.getLocation().clone().add(0.0, 0.75, 0.0));
    }
}
