package me.nekocloud.punishment.bungee;

import lombok.Getter;

/**
 * @author xwhilds
 */
public final class BungeePunishment {

	@Getter
	private static BungeePunishment instance;

	public void onLoad() {
		instance = this;
	}

	public void onEnable() {

	}
}
