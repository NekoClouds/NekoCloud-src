package me.nekocloud.nekoapi.donatemenu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.nekoapi.donatemenu.event.AsyncGamerSendFastMessageEvent;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum FastMessage {
    GO("┌( ಠ_ಠ)┘"),
    SLEEP("－ω－) zzZ"),
    CAPITULATE("(×_×)尸"),
    MAJOR("$ (ಠ_ಠ) $"),
    ENRAGES("─=≡Σ((( つ＞＜)つ"),
    HELLO("(^ω^)ノ"),
    GOODBYE("╰(╯︵╰,)"),
    HUGGING("(ノ^_^)ノ"),
    SAD("(╯︵╰,)"),
    JEEZ("ヽ(ﾟ〇ﾟ)ﾉ"),
    WHY("＼(〇_ｏ)／"),
    ACCESSIBLY("┐(︶▽︶)┌"),
    DONT_STUPID("(; -_-)"),
    NICELY("(＾• ω •＾)"),
    OFFENSIVELY("(个_个)"),
    WTF("(⊙_⊙)"),
    KILLED("(ﾒ￣▽￣)︻┳═一"),
    DANCING("┌(^_^)┘"),
    XZ("¯＼_(ツ)_/¯"),
    EATING("( ˘▽˘)っ♨"),
    GO_AWAY("( ╯°□°)╯ ┻━━┻"),
    MUSIC("(￣▽￣)/♫¸¸♪"),
    FUCK("└(￣-￣└)"),
    WOW("٩(◕‿◕)۶"),
    LOVE("( ˘⌣˘)♡"),
    LAGS("(ノ°益°)ノ"),
    EASY("<(￣︶￣)>"),
    GIVE_RESOURCES("凸◟(º o º )"),
    ;

    private final String smile;

    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    public String getKey() {
        return "FAST_MESSAGE_" + name();
    }

    public Group getGroup() {
        return Group.AKIO;
    }

    public void sendToAll(BukkitGamer gamer) {
        if (gamer == null || gamer.getPlayer() == null) {
            return;
        }

        AsyncGamerSendFastMessageEvent event = new AsyncGamerSendFastMessageEvent(gamer,
                this, new HashSet<>(gamerManager.getGamers().values()));
        BukkitUtil.runTaskAsync(() -> BukkitUtil.callEvent(event));
    }

    public static Map<String, FastMessage> getMessages(Language lang) {
        Map<String, FastMessage> map = new HashMap<>();
        Arrays.stream(values()).forEach(fastMessage -> map.put(lang.getMessage(fastMessage.getKey()), fastMessage));
        return map;
    }
}
