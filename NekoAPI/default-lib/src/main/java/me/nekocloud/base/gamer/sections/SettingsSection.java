package me.nekocloud.base.gamer.sections;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import lombok.val;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

public class SettingsSection extends Section {

    @Getter
    private Language language = Language.DEFAULT;

    private final Object2BooleanMap<SettingsType> settings = new Object2BooleanOpenHashMap<>();

    //для БД, чтобы не делать 10 запросов в БД, тут мы храним те данные, которые в самой БД
    private final Int2IntMap duplicatedSettings = Int2IntMaps.synchronize(new Int2IntOpenHashMap());

    public SettingsSection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    @Override
    public boolean loadData() {
        val settings = GlobalLoader.getSettings(baseGamer.getPlayerID());
        this.language = settings.getFirst().getFirst();

        if (settings.getFirst().getSecond()) { //проверяем, из БД ли эта инфа или нет
            duplicatedSettings.put(SettingsType.LANGUAGE_KEY, language.getId());
        }

        for (val data : settings.getSecond().object2BooleanEntrySet()) {
            val settingsType = data.getKey();
            val value = data.getBooleanValue();

            duplicatedSettings.put(data.getKey().getKey(), data.getValue() ? 1 : 0);

            if (settingsType == SettingsType.FLY && !baseGamer.isAkio()) {
                this.settings.put(settingsType, false);
                continue;
            }

            if (settingsType == SettingsType.MUSIC && !baseGamer.isTrival()) {
                this.settings.put(settingsType, false);
                continue;
            }

            if (settingsType == SettingsType.TEAM_GLOWING && !baseGamer.isTrival()) {
                this.settings.put(settingsType, false);
                continue;
            }

            if (settingsType == SettingsType.HUB_GLOWING && !baseGamer.isAkio()) {
                this.settings.put(settingsType, false);
                continue;
            }

            this.settings.put(settingsType, value);
        }

        this.settings.putIfAbsent(SettingsType.CHAT, true);
        this.settings.putIfAbsent(SettingsType.BOARD, true);
        this.settings.putIfAbsent(SettingsType.PRIVATE_MESSAGE, true);
        this.settings.putIfAbsent(SettingsType.FRIENDS_REQUEST, true);
        this.settings.putIfAbsent(SettingsType.GUILD_REQUEST, true);
        this.settings.putIfAbsent(SettingsType.PARTY_REQUEST, true);
        this.settings.putIfAbsent(SettingsType.AUTO_MESSAGE_ANNOUNCE, true);
        this.settings.putIfAbsent(SettingsType.DISCORD_BOT_ANNOUNCE, true);
        this.settings.putIfAbsent(SettingsType.DISCORD_LEAK, false);
        this.settings.putIfAbsent(SettingsType.VK_BOT_ANNOUNCE, true);
        this.settings.putIfAbsent(SettingsType.VK_LEAK, false);
        this.settings.putIfAbsent(SettingsType.VANISHED, false );

        if (!baseGamer.isDefault()) {
            this.settings.putIfAbsent(SettingsType.DONATE_CHAT, true);
        }

        if (baseGamer.isAkio()) {
            this.settings.putIfAbsent(SettingsType.FLY, true);
        }

        if (baseGamer.isTrival()) {
            this.settings.putIfAbsent(SettingsType.MUSIC, true);
        }

        return true;
    }

    public boolean getSetting(SettingsType type) {
        if (type == null) return false;

        return settings.getOrDefault(type, false);
    }

    public void setSetting(SettingsType type, boolean value, boolean saveToMysql) {
        if (type == null)
            return;

        val saveValue = settings.get(type);
        if (saveValue != null && saveValue == value)
            return;

        settings.put(type, value);

        if (saveToMysql) save(type.getKey(), (value ? 1 : 0));

        this.duplicatedSettings.put(type.getKey(), (value ? 1 : 0));
    }

    public void setLang(Language language, boolean saveToMysql) {
        if (this.language.getId() == language.getId())
            return;

        this.language = language;

        if (saveToMysql) save(SettingsType.LANGUAGE_KEY, language.getId());

        this.duplicatedSettings.put(SettingsType.LANGUAGE_KEY, language.getId());
    }

    private void save(int type, int value) {
        val playerID = baseGamer.getPlayerID();
        GlobalLoader.setSetting(playerID, type, value, !duplicatedSettings.containsKey(type));
    }


    static {
        new TableConstructor("settings",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true),
                new TableColumn("setting_id", ColumnType.INT_5).primaryKey(true),
                new TableColumn("setting_value", ColumnType.INT_5)
        ).create(GlobalLoader.getMysqlDatabase()); // + добавить индексы
    }

}