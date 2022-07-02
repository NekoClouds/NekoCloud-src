package me.nekocloud.base.gamer.sections;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.base.util.Pair;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseSection extends Section {

    int playerID    = -1;
    Group group     = Group.DEFAULT;
    String prefix   = Group.DEFAULT.getPrefix();

    public BaseSection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    @Override
    public boolean loadData() {
        Pair<String, Integer> playerInfo = GlobalLoader.getInfo(baseGamer.getName());
        playerID = playerInfo.getSecond();

        if (baseGamer.isOnline()) {
            if (playerID == -1) {
                playerID = GlobalLoader.getPlayerID(baseGamer.getName());
            }
            if (!baseGamer.getName().equals(playerInfo.getFirst())) {
                GlobalLoader.setPlayerName(playerID, baseGamer.getName());
            }
        } else {
            baseGamer.setName(playerInfo.getFirst());
        }

        if (playerID != -1) {
            group = GlobalLoader.getGroup(playerID);
            prefix = GlobalLoader.getPrefix(playerID, group);
        }

        return playerID != -1;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setGroup(Group group) {
        setGroup(group, true);
    }

    // Если на коре ставим
    public void setGroup(Group group, boolean mysql) {
        if (group == null || this.group == group) {
            return;
        }

        if (this.group.getPrefix().equalsIgnoreCase(this.prefix)) {
            this.prefix = group.getPrefix();
        }

        this.group = group;

        if (mysql) {
            GlobalLoader.setGroup(playerID, group.getId());
        }
    }

    public void savePrefix(String prefix, String charPrefix) {
        this.prefix = prefix;
        GlobalLoader.setPrefix(baseGamer.getPlayerID(), charPrefix);
    }


    static {
        new TableConstructor("identifier",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true).unigue(true),
                new TableColumn("player_name", ColumnType.VARCHAR_16),
                new TableColumn("player_real_name", ColumnType.VARCHAR_16)
        ).create(GlobalLoader.getMysqlDatabase());
        new TableConstructor("temp_prefixes",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("prefix", ColumnType.VARCHAR_32),
                new TableColumn("expires", ColumnType.TIMESTAMP)
        ).create(GlobalLoader.getMysqlDatabase());
        new TableConstructor("players_groups",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("group_id", ColumnType.INT_11).primaryKey(true),
                new TableColumn("prefix", ColumnType.INT_5).primaryKey(true)
        ).create(GlobalLoader.getMysqlDatabase());
        new TableConstructor("custom_prefixes",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("prefix", ColumnType.VARCHAR_32)
        ).create(GlobalLoader.getMysqlDatabase());
    }

}
