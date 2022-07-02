package me.nekocloud.core.common.auth.sql;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QueryType {
	INSERT_PLAYER_QUERY("INSERT INTO `auth_data` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"),
	INJECT_PLAYER_QUERY("SELECT * FROM `auth_data` WHERE `Id`=?"),
	DEL_PLAYER_QUERY("DELETE FROM `auth_data` WHERE `Id`=?"),

	UPD_PASSWORD_QUERY("UPDATE `auth_data` SET `Password`=? WHERE `Id`=?"),
	UPD_LICENSE_QUERY("UPDATE `auth_data` SET `License`=? WHERE `Id`=?"),
	UPD_DISCORD_QUERY("UPDATE `auth_data` SET `DiscordId`=? WHERE `Id`=?"),
	UPD_VK_QUERY("UPDATE `auth_data` SET `VkId`=? WHERE `Id`=?"),
	UPD_LAST_ADDRESS_QUERY("UPDATE `auth_data` SET `LastAddress`=? WHERE `Id`=?"),
	UPD_SESSION_TIME_QUERY("UPDATE `auth_data` SET `ExpireSessionTime`=? WHERE `Id`=?"),
	UPD_CODE_TYPE_QUERY("UPDATE `auth_data` SET `CodeType`=? WHERE `Id`=?"),

	GET_CODE_TYPE_QUERY("SELECT * FROM `auth_data` WHERE `Id`=?"),
	GET_DISCORD_TAG_QUERY("SELECT * FROM `discord_data` WHERE `DiscordId`=?"),
	GET_LICENSE_QUERY("SELECT * FROM `license_data` WHERE `UUID`=?"),
	;

	private final String query;

	public final String get() {
		return query;
	}
}
