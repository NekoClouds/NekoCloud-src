package me.nekocloud.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CoreSql {

    NekoCore nekoCore;

    @Getter
    static MySqlDatabase database;
    static Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    public CoreSql(final NekoCore nekoCore) {
        this.nekoCore = nekoCore;
        database = MySqlDatabase.newBuilder()
                .data("core")
                .host(ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .user("neko")
                .create();

        initTables();
    }

    void close() {
        database.close();
    }

    // Создаем бд если их не существует
    void initTables() {
        new TableConstructor("punishment_data",
                new TableColumn("intruder_id", ColumnType.INT_11),
                new TableColumn("owner_id", ColumnType.INT_11),
                new TableColumn("type", ColumnType.INT_2),
                new TableColumn("reason", ColumnType.VARCHAR_128),
                new TableColumn("time", ColumnType.TIMESTAMP)
        ).create(database);
        new TableConstructor("punishment_history",
                new TableColumn("intruder_id", ColumnType.INT_11),
                new TableColumn("owner_id", ColumnType.INT_11),
                new TableColumn("type", ColumnType.INT_2),
                new TableColumn("reason", ColumnType.VARCHAR_128),
                new TableColumn("time", ColumnType.TIMESTAMP)
        ).create(database);
    }
}
