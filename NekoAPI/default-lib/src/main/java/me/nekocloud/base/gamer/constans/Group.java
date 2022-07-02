package me.nekocloud.base.gamer.constans;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum Group {
    PIDOR(-1, "§a§lПИДОР", "§a§lPIDOR", "pidor", "§a§lPIDOR §a", "§c§oLG§e§oBT§a§oQI§5§oA+ §8 »§d", -1),
    DEFAULT(0, "§7Игрок", "§7Default", "default", "§7", "§8 »§7", 0),

    HEGENT(100, "§9§lHEGENT", "§9§lHEGENT", "hegent", "§9§lHEGENT §9", "§8 »§7", 1),
    AKIO(200, "§e§lAKIO", "§e§lAKIO", "akio", "§e§lAKIO §e", "§8 »§7", 2),
    TRIVAL(300, "§b§lTRIVAL", "§b§lTRIVAL", "trival", "§b§lTRIVAL §b", "§8 »§7", 3),
    AXSIDE(400, "§c§lAXSIDE", "§c§lAXSIDE", "axside", "§c§lAXSIDE §c", "§8 »§7", 4),
    NEKO(500, "§d§lNEKO", "§d§lNEKO", "neko", "§d§lNEKO §d", "§8 »§f", 5),

    YOUTUBE(600, "§6§lYOUTUBE", "§6§lYOUTUBE", "youtube", "§6§lYOUTUBE §6", "§8 »§f", 6),
    TIKTOK(610, "§5§lTIKTOK", "§5§lTIKTOK", "tiktok", "§5§lTIKTOK §5", "§8 »§f", 6),

    BUILDER(650, "§3Строитель", "§3§lBUILDER", "builder", "§3§lBUILDER §3", "§8 »§f", 5),
    SRBUILDER(660, "§3Ст. Строитель", "§3§lSR. BUILDER", "srbuilder", "§3§lSR. BUILDER §3", "§8 »§f", 5),

    JUNIOR(680, "§2§lМл. Модератор", "§2§lJUNIOR", "junior", "§2§lJUNIOR §2", "§8 »§f", 7),
    MODERATOR(700, "§9Модератор", "§9§lMODERATOR", "moderator", "§9§lMODER §9", "§8 »§f", 8),
    SRMODERATOR(750, "§9Ст. Модератор", "§9§lSR. MODERATOR", "srmoderator", "§9§lSR. MODER §9", "§8 »§f", 9),

    ADMIN(900, "§4Админ", "§4§lADMIN", "administrator", "§4§lADMIN §4", "§8 »§f", 10),

    // Онли для незукоко
    OWNER(1000, "§4Владелец", "§4§lOWNER", "owner", "§4§lOWNER §4", ADMIN.getSuffix(), 100),

    // Онли для новита
    DEVELOPER(1100, "§bРазработчик", "§b§lDEV", "developer", "§b§lDEV §b", ADMIN.getSuffix(), 110);

    int id;
    String name, nameEn, groupName, prefix, suffix;
    int level;

    public String getLocaleName(final @NotNull Language language) {
        if (language == Language.DEFAULT)
            return name;

        return nameEn;
    }

    static final Int2ObjectMap<Group> GROUPS = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    public static Group getGroup(final int groupID) {
        val group = GROUPS.get(groupID);
        if (group != null) {
            return group;
        }

        return DEFAULT;
    }

    public static Group getGroupByLevel(final int level) {
        return GROUPS.values().stream()
                .filter(g -> g.getLevel() == level)
                .findFirst()
                .orElse(Group.DEFAULT);
    }

    public static Group getGroupByName(final @NotNull String name) {
        return GROUPS.values().stream()
                .filter(g -> g.getGroupName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(Group.DEFAULT);
    }

    public static Group getNextGroup(final @NotNull Group group, final int amount) {
        int find = group.ordinal() + amount;
        if (find + 1 > GROUPS.size()) {
            return group;
        }

        return GROUPS.values().stream()
                .filter(g2 -> g2.ordinal() == find)
                .findFirst()
                .orElse(group);
    }

    public static Group getNextGroup(final @NotNull Group group) {
        return Group.getNextGroup(group, 1);
    }

    public boolean isStaff() {
        return this.getLevel() >= SRMODERATOR.getLevel();
    }

    @Override
    public String toString() {
        return nameEn;
    }

    static {
        for (val group : values()) {
            GROUPS.put(group.getId(), group);
        }
    }
}
