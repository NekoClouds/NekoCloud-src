package me.nekocloud.vkbot.command;

import lombok.SneakyThrows;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.connection.server.BukkitServer;
import me.nekocloud.core.connection.server.BungeeServer;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class TestPlayer extends VkCommand{

	public TestPlayer() {
		super("plcreate");
		setGroup(Group.ADMIN);
	}

	@Override
	@SneakyThrows
	protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
		if (args.length == 0)
			return;
		if (args[0].startsWith("-")) {
			NekoCore.getInstance().handlePlayerDisconnection(NekoCore.getInstance().getNetworkManager().getPlayerID(args[0].substring(1)));
			return;
		}

		if (NekoCore.getInstance().getPlayer(args[0]) != null)
			return;

		val corePlayer = CorePlayer.getOrCreate(args[0], "0.0.0.0",
				new BungeeServer("bungee-2", 13377), 1);

		NekoCore.getInstance().handlePlayer(corePlayer);
		corePlayer.setBukkit(new BukkitServer("MuskulovTestServer", 228));
	}
}
