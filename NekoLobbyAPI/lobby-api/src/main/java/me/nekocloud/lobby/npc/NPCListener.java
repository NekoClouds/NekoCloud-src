package me.nekocloud.lobby.npc;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.lobby.config.GameConfig;
import me.nekocloud.lobby.game.data.Channel;
import me.nekocloud.lobby.game.guis.SpectatorGui;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.Map;

public class NPCListener extends DListener<Lobby> {

    private final Map<NPC, LobbyNPC> npcs;

    private final TIntObjectMap<SpectatorGui> spectatorGuis = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    public NPCListener(Lobby lobby, GameConfig gameConfig) {
        super(lobby);
        this.npcs = gameConfig.getAllNpc();

        Arrays.stream(Language.values()).forEach(lang -> {
            spectatorGuis.put(lang.getId(), new SpectatorGui(gameConfig, lang));
        });

//        Bukkit.getScheduler().runTaskTimerAsynchronously(lobby, () -> {
//            gameConfig.getChannels().values().forEach(channel -> {
//                channel.update(lobby.getServerStreamer());
//                channel.getGuis().valueCollection().forEach(GameGui::update);
//            });
//            spectatorGuis.valueCollection().forEach(GameGui::update);
//        }, 0, 20 * 5);//каждые 5 сек вызываю и все обновляю
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinPlayer(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        spawn(gamer, gamer.getLanguage());
    }

    @EventHandler
    public void onClickNPC(GamerInteractNPCEvent e) {
        BukkitGamer gamer = e.getGamer();
        if (Cooldown.hasOrAddCooldown(gamer, "toServer", 20)) {
            return;
        }
        NPC npc = e.getNpc();

        LobbyNPC lobbyNPC = npcs.get(npc);
        if (lobbyNPC == null) {
            return;
        }

        if (lobbyNPC instanceof StartGameNPC startGameNPC) {

            Channel channel = startGameNPC.getChannel();
            if (e.getAction() == GamerInteractNPCEvent.Action.LEFT_CLICK) {
                channel.sendToBestServer(gamer);
            } else {
                channel.getChannelGui(gamer).open(gamer);
            }
        }

    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        BukkitGamer gamer = e.getGamer();
        Language oldLang = e.getOldLanguage();
        Language lang = e.getLanguage();

        BukkitUtil.runTaskAsync(() -> {
            npcs.values().forEach(lobbyNPC ->
                    lobbyNPC.getHologram(oldLang).removeTo(gamer));

            spawn(gamer, lang);
        });
    }

    private void spawn(BukkitGamer gamer, Language lang) {
        npcs.values().forEach(lobbyNPC ->
                lobbyNPC.getHologram(lang).showTo(gamer));
    }
}

