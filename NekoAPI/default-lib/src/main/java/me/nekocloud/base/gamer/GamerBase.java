package me.nekocloud.base.gamer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.gamer.sections.SettingsSection;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public abstract class GamerBase extends IBaseGamerImpl implements OnlineGamer {

    private static final Set<String> DEVELOPERS = ImmutableSet.of(
            "_Novit_",
            "NezukoCo",
            "llonnections",
            "hepller",
            "Xakin"
    );
    private final Table<String, String, Object> databasesValuesCacheTable = HashBasedTable.create();


    protected GamerBase(String name) {
        super(name);

        initSection(SettingsSection.class);

        for (val section : getSections().values()) {
            if (section == null)
                return;

            if (section.loadData()) {
                continue;
            }

            throw new IllegalArgumentException("кажется что-то пошло не так, секция - "
                    + section.getClass().getSimpleName() + ", ник игрока - " + name);
        }
    }

    public void sendMessages(List<String> messages) {
        messages.forEach(this::sendMessage);
    }

    public void sendMessageLocale(String key, Object... replaced) {
        sendMessage(this.getLanguage().getMessage(key, replaced));
    }

    public void sendMessagesLocale(String key, Object... replaced) {
        sendMessages(this.getLanguage().getList(key, replaced));
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isDeveloper() {
        return getGroup().getLevel() >= Group.ADMIN.getLevel()
                && DEVELOPERS.contains(this.name);
    }

    @Override
    public boolean isOwner() {
        return getGroup().getLevel() >= Group.OWNER.getLevel()
                && DEVELOPERS.contains(this.name);
    }

    @Override
    public Language getLanguage() {
        return getSection(SettingsSection.class).getLanguage();
    }

    @Override
    public String toString() {
        return "OnlineGamer{name=" + this.getName() + '}';
    }

    @Override
    public void remove() {
        GamerAPI.removeGamer(name);
    }

    @Override
    public boolean isAdmin() {
        return getGroup().getLevel() >= Group.ADMIN.getLevel();
    }

    @Override
    public boolean isTester() {
        return getPrefix().toLowerCase().contains("test");
    }

    public boolean getSetting(SettingsType type) {
        return getSection(SettingsSection.class).getSetting(type);
    }

    public void setSetting(SettingsType type, boolean key) {
        val settingsSection = getSection(SettingsSection.class);
        if (key == settingsSection.getSetting(type)) {
            return;
        }

        settingsSection.setSetting(type, key, true);
    }

    // TODO: Дописать, очень крутая штука.
    @SuppressWarnings("all")
    @SneakyThrows
    @Override
    public <T> T getDatabaseValue(@NotNull String table, @NotNull String column) {
        if (databasesValuesCacheTable.contains(table, column)) {
            return (T) databasesValuesCacheTable.get(table, column);
        }

        if (GlobalLoader.getMysqlDatabase().getData() == null) {
            return (T)(Object)0;
        }

        T value = (T) GlobalLoader.getMysqlDatabase()
                .executeQuery("SELECT `" + column + "` FROM `"
                        + table + "` WHERE `playerID`=?", resultSet -> {

                    if (!resultSet.next())
                        return 0;

                    return resultSet.getObject(column);

                }, getPlayerID());

        databasesValuesCacheTable.put(table, column, value);
        return value;
    }
}
