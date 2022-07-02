package me.nekocloud.base.gamer.constans;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public enum Version {
    EMPTY(-1),

    //--1.8--//
    V_1_8(47),

    //--1.9--//
    V_1_9(107),
    V_1_9_1(108),
    V_1_9_2(109),
    V_1_9_3_AND_1_9_4(110),

    //--1.10--//
    V_1_10(210),

    //--1.11--//
    V_1_11(315),
    V_1_11_1_AND_1_11_2(316),

    //--1.12--//
    V_1_12(335),
    V_1_12_1(338),
    V_1_12_2(340),

    //--1.13--//
    V_1_13(393),
    V_1_13_1(401),
    V_1_13_2(404),

    //--1.14--//
    V_1_14(477),
    V_1_14_1(480),
    V_1_14_2(485),

    //--1.15--//
    V_1_15(573),
    V_1_15_1(575),
    V_1_15_2(578),

    //--1.16--//
    V_1_16(735),
    V_1_16_1(736),
    V_1_16_2(751),
    V_1_16_3(753),
    V_1_16_4(754),
    V_1_16_5(754),

    //--1.17--//
    V_1_17(755),
    V_1_17_1(756),

    //--1.18--//
    V_1_18(757),
    V_1_18_1(757),
    V_1_18_2(758),

    //--1.19--//
    V_1_19(759),
    V_1_19_1(760),

    //--1.20--// //WTF
    ;

    private final int protocol;

    static final Int2ObjectMap<Version> VERSIONS = new Int2ObjectOpenHashMap<>();

    /**
     * Получает версию по протоколу
     * @param protocol протокол
     * @return имя енама из мапы
     */
    public static Version getVersion(int protocol) {
        val version = VERSIONS.get(protocol);
        if (version != null) {
            return version;
        }

        return EMPTY;
    }

    static {
        for (val version : values()) {
            VERSIONS.put(version.protocol, version);
        }
    }

    public @NotNull String toClientName() {
        return name().substring(2).replace("_", ".");
    }

}
