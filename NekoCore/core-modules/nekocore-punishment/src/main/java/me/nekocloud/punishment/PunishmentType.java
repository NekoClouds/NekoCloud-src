package me.nekocloud.punishment;

/**
 * @author xwhilds
 */
public enum PunishmentType {
    KICK, WARN,

    TEMP_BAN, PERMANENT_BAN,
    TEMP_MUTE, PERMANENT_MUTE,

    ANTI_CHEAT_TEMP, ANTI_CHEAT_PERMANENT;

    public static final PunishmentType[] PUNISHMENT_TYPES = values();
}
