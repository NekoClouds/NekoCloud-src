package me.nekocloud.base.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.base.gamer.OnlineGamer;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@AllArgsConstructor
@Getter
public enum SubType {

    MISC(0, null, "unknown", "UNKNOWN_SERVER_TYPE", GameType.UNKNOWN),
    LOBBY(1, null, "Lobby", "LOBBY_SERVER_TYPE", GameType.HUB),
    
    SWS(10, TypeGame.SOLO, "Solo", "SKYWARS_SOLO_TYPE", GameType.SW),
    SWD(11, TypeGame.DOUBLES, "Doubles", "SKYWARS_DUO_TYPE", GameType.SW),
    BWS(20, TypeGame.SOLO, "Solo", "BEDWARS_SOLO_TYPE", GameType.BW),
    BWD(21, TypeGame.DOUBLES, "Doubles", "BEDWARS_DUO_TYPE", GameType.BW),
    BWT(22, TypeGame.TEAM, "Team", "BEDWARS_TEAM_TYPE", GameType.BW),
    EWS(30, TypeGame.SOLO, "Solo", "EGGWARS_SOLO_TYPE", GameType.EW),
    EWD(31, TypeGame.DOUBLES, "Doubles", "EGGWARS_DUO_TYPE", GameType.EW),
    EWT(32, TypeGame.TEAM, "Team", "EGGWARS_TEAM_TYPE", GameType.EW),
    LWS(50, TypeGame.SOLO, "Solo", "LUCKYWARS_SOLO_TYPE", GameType.LW),
    LWD(51, TypeGame.DOUBLES, "Doubles", "LUCKYWARS_DUO_TYPE", GameType.LW),
    PRS(70, TypeGame.SOLO, "Solo", "PARKOURRACERS_SOLO_TYPE", GameType.PR),
    PRD(71, TypeGame.DOUBLES, "Doubles", "PARKOURRACERS_DUO_TYPE", GameType.PR),
    PRC(72, TypeGame.SOLO, "Classic", "PARKOURRACERS_CLASSIC_TYPE", GameType.PR),
    BBS(73, TypeGame.SOLO, "BuildBattle Solo", "BUILDBATTLE_SOLO_TYPE", GameType.ARCADE),
    BBD(74, TypeGame.DOUBLES, "BuildBattle Doubles", "BUILDBATTLE_DUO_TYPE", GameType.ARCADE),
    HNS(75, TypeGame.SOLO, "HideAndSeek Classic", "HIDE_AND_SEEK_GAME", GameType.ARCADE),
    SBS(76, TypeGame.SOLO, "SpeedBuilders Solo", "SPEED_BUILDERS_SOLO_TYPE", GameType.ARCADE),
    SBD(77, TypeGame.DOUBLES, "SpeedBuilders Doubles", "SPEED_BUILDERS_DOUBLES_TYPE", GameType.ARCADE);

    public static SubType current = MISC;
    
    private static final Map<GameType, List<SubType>> TYPES = new EnumMap<>(GameType.class);
    private static final Map<String, SubType> TYPES_BY_NAME = new HashMap<>();
    
    private final int id;
    private final TypeGame typeGame;
    private final String typeName;
    private final String localeName;
    
    private final GameType gameType;
    private final GameUpdateType updateType;

    SubType(int id, TypeGame typeGame, String typeName, String localeName, GameType gameType) {
        this(id, typeGame, typeName, localeName, gameType, gameType.getUpdateType());
    }

    public GameUpdateType getUpdateType() {
        return this.updateType;
    }

    public @NotNull String getName(OnlineGamer gamer) {
        if (gamer != null) {
            return gamer.getLanguage().getMessage(this.localeName);
        }

        return Language.DEFAULT.getMessage(this.localeName);
    }

    public static @NotNull List<SubType> ofMode(GameType type) {
        List<SubType> subTypes = TYPES.get(type);
        if (subTypes == null) {
            return Collections.emptyList();
        }
        
        return subTypes;
    }

    public static SubType getByName(@NotNull String name) {
        return TYPES_BY_NAME.getOrDefault(name.toLowerCase(), MISC);
    }

    static {
        for (SubType value : SubType.values()) {
            TYPES_BY_NAME.put(value.name().toLowerCase(), value);
            TYPES.computeIfAbsent(value.getGameType(), k -> new ArrayList<>()).add(value);
        }
    }
}

