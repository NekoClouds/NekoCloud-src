package me.nekocloud.modules.domaintracker;

import lombok.Getter;
import me.nekocloud.modules.domaintracker.command.TrackerCommand;
import me.nekocloud.modules.domaintracker.listener.TrackerListener;
import me.nekocloud.modules.domaintracker.redis.IRedisDataManager;
import me.nekocloud.modules.domaintracker.redis.RedisDataManager;
import net.md_5.bungee.api.plugin.Plugin;

@Getter

public class DomainTracker extends Plugin {

	// todo хранить в редисе коннекты в течении дня
	private final IRedisDataManager redisData = new RedisDataManager();

	@Override
	public void onEnable() {

		new TrackerCommand(this);
		new TrackerListener(this);
	}

}
