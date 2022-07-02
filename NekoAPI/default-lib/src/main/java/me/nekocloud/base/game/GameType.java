package me.nekocloud.base.game;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.skin.Skin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GameType {

    UNKNOWN(1, "misc", false, "misc", "misc", "", null),

    //main
    HUB(2, "Hub", false, "hub", "hub", "", null),
    AUTH(3, "Auth", false, "auth", "auth", "", null),
    LIMBO(4,"Limbo", false, "limbo", "limbo", "", null),

    // miniGames
    SW(10, "SkyWars", true, "swlobby", "sw", "GAMEMENU_SKYWARS_LORE", GameSkin.SW),
    BW(20, "BedWars", true, "bwlobby", "bw", "GAMEMENU_BEDWARS_LORE", GameSkin.BW),
    LW(30, "LuckyWars", true, "lwlobby", "lw", "GAMEMENU_LUCKYWARS_LORE", GameSkin.LW),
    EW(40, "EggWars", true, "ewlobby", "ew", "GAMEMENU_EGGWARS_LORE", GameSkin.EW, GameUpdateType.UPDATE),
    PR(50, "ParkourRacers", true, "prlobby", "pr", "GAMEMENU_PARKOUR_LORE", GameSkin.PR),
    ARCADE(60, "ArcadeGames", true, "arlobby", "arcade", "GAMEMENU_ARCADE_LORE", GameSkin.ARCADE),

    // survival
    GRIEF(200, "Grief", false, "grief", "grief", "GAMEMENU_CREATIVE_LORE", GameSkin.CREATIVE),
    ANARCHY(300, "Anarchy", false, "anarchy", "anarchy", "GAMEMENU_ANARCHY_LORE", GameSkin.ANARCHY, GameUpdateType.WIPE),
    SB(400, "SkyBlock", false, "skyblock", "sb", "GAMEMENU_SB_LORE", GameSkin.SB, GameUpdateType.WIPE),
    SURVIVAL(500, "Survival", false, "survival", "survival", "GAMEMENU_SURVIVAL_LORE", GameSkin.SURVIVAL, GameUpdateType.SOON);

    public static GameType current = UNKNOWN;

    int id;
    String name;

    boolean miniGame;

    String lobbyChannel;
    String channel;
    String loreKey;

    Skin skin;
    GameUpdateType updateType;

    GameType(int id, String name, boolean miniGame, String lobbyChannel, String channel, String loreKey, Skin skin) {
        this(id, name, miniGame, lobbyChannel, channel, loreKey, skin, GameUpdateType.DEFAULT);
    }

    public Skin getSkin() {
        if (skin != null) {
            return skin;
        }
        return Skin.DEFAULT_SKIN;
    }

    public @NotNull List<String> getLore(@NotNull Language language) {
        return language.getList(loreKey);
    }

    public static GameType getType(@NotNull String server) {
        for (val gameType : GameType.values()) {
            if (server.toLowerCase().startsWith(gameType.channel.toLowerCase())) {
                return gameType;
            }
        }
        return UNKNOWN;
    }

    public static boolean isTyped(@NotNull String server, @NotNull SubType mode) {
        return getType(server).equals(mode.getGameType());
    }

    public static boolean isTyped(@NotNull String server, @NotNull GameType mode) {
        return getType(server).equals(mode);
    }

}

