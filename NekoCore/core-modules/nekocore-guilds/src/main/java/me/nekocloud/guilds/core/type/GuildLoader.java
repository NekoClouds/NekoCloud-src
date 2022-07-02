package me.nekocloud.guilds.core.type;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.core.CoreSql;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GuildLoader {

    MySqlDatabase coreSql = CoreSql.getDatabase();
    boolean load = false;

    public void init() {
        if (load) {
            return;
        }

    }

}
