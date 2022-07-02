package me.nekocloud.auth.sql;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.sql.ConnectionConstants;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.nekocloud.auth.sql.QueryType.GET_LICENSE_QUERY;

@Getter
@FieldDefaults(makeFinal = true)
public class AuthSql {

	@Getter
	static MySqlDatabase database;
	static long EXPIRE_SESSION_MILLIS = TimeUnit.DAYS.toMillis(3);

	public AuthSql() {
		database = MySqlDatabase.newBuilder()
                .data("core")
                .host(ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .user("neko")
                .create();

		initTables();
	}

	void initTables() {
		new TableConstructor("license_data",
				new TableColumn("UUID", ColumnType.VARCHAR_128).primaryKey(true).unigue(true),
				new TableColumn("license", ColumnType.BOOLEAN)
		).create(database);
		new TableConstructor("discord_data",
				new TableColumn("Id", ColumnType.INT_11).unigue(true).primaryKey(true),
                new TableColumn("DiscordID", ColumnType.BIG_INT),
                new TableColumn("DiscordTag", ColumnType.VARCHAR_64)
        ).create(database);
		new TableConstructor("auth_data",
                new TableColumn("Id", ColumnType.INT_11).unigue(true).primaryKey(true),
                new TableColumn("Password", ColumnType.VARCHAR_128),
                new TableColumn("VkId", ColumnType.INT),
                new TableColumn("DiscordID", ColumnType.INT_11),
                new TableColumn("CodeType", ColumnType.TINY_INT),
                new TableColumn("RegisterDate", ColumnType.TIMESTAMP),
                new TableColumn("ExpireSessionTime", ColumnType.DATETIME),
                new TableColumn("RegisterAddress", ColumnType.VARCHAR_64),
                new TableColumn("LastAddress", ColumnType.VARCHAR_64)
        ).create(database);
	}

	/**
	 * Получить информацию о том, имеет ли игрок лицензию или нет
	 * @param player_uuid юайди подключения
	 * @return true or false
	 */
	public static boolean getLicenseData(UUID player_uuid) {
		return database.executeQuery(GET_LICENSE_QUERY.get(), resultSet -> {
            if (!resultSet.next())
                return false;

            return resultSet.getBoolean("license");
        }, player_uuid);
	}
}
