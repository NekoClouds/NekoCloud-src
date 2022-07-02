package me.nekocloud.auth.commands;

import com.google.common.collect.ImmutableSet;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.manager.AuthManager;
import me.nekocloud.base.gamer.constans.Group;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.commands.ProxyCommand;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

@Log4j2
public class AdminAuthCommand extends ProxyCommand<BungeeAuth> {

	public AdminAuthCommand(BungeeAuth plugin) {
		super(plugin, "auth");
		setGroup(Group.ADMIN);
	}

	@Override
	public void execute(final BungeeEntity entity, final String[] args) {
		if (args.length < 1) {
			entity.sendMessage(
     			"""
					§d§lАВТОРИЗАЦИЯ §8| §fДоступные команды:
					
					§5     §f/auth unreg <ник> - разрегать игрока
					§5     §f/auth license <ник> on/off - управление лицензией игрока
					§5     §f/auth reg <ник> <пароль> - форсированно зарегать игрока
					§5     §f          (даже если его нету в идентификаторе)
					§5     §f/auth cp <ник> <новый_пароль> - сменить пароль игроку
					""");
			return;
		}

		switch (args[0].toLowerCase()) {
			case "unreg", "unregister" -> {
				if (args.length < 2) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, используй: /auth unreg <ник>");
					return;
				}

				val targetUser = AuthManager.INSTANCE.loadOrCreate(args[1]);
				if (targetUser == null) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, игрок " + args[1] + " не зарегистрирован!");
					return;
				}

				val targetGamer = BungeeGamer.getOrCreate(args[1],
						new InetSocketAddress("0.0.0.0", 0).getAddress());
				if (targetGamer.getGroup().getLevel() >= Group.ADMIN.getLevel()) {
					entity.sendMessage("§cВы не можете взаимодействовать с данным игроком.");
					targetGamer.disconnect();
					return;
				}

				entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fВы успешно разрегистрировали игрока " + args[1]);
				log.info("[ACTION] Admin " + entity.getName() + " unregister player " + args[1]);
				targetUser.setNewPassword(null);
				targetGamer.disconnect();
			}

			case "license" -> {
				if (args.length < 2) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, используй: /auth license <ник> on/off");
					return;
				}

				val targetUser = AuthManager.INSTANCE.loadOrCreate(args[1]);
				if (targetUser == null) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, игрок " + args[1] + " не зарегистрирован!");
					return;
				}

				if (targetUser.getHandle().getGroup().getLevel() >= Group.ADMIN.getLevel()) {
					entity.sendMessage("§сВы не можете взаимодействовать с данным игроком.");
					return;
				}

				switch (args[2].toLowerCase()) {
					case "on" -> {
						entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fЛицензия §aвключена §f для игрока" + args[1]);
						log.info("[ACTION] Admin " + entity.getName() + " turn on license account for " + args[1]);
					}

					case "off" -> {
						entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fЛицензия §cотключена §f для игрока" + args[1]);
						log.info("[ACTION] Admin " + entity.getName() + " turn on license account for " + args[1]);
					}
				}
			}

			case "reg", "register" -> {
				if (args.length < 2) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, используй: /auth register <ник> <пароль>");
					return;
				}

				val targetUser = AuthManager.INSTANCE.loadOrCreate(args[1]);
				if (targetUser == null) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, игрок " + args[1] + " не зарегистрирован!");
					return;
				}

				val password = AuthManager.INSTANCE.hashPassword(args[2]);
				try {
					AuthManager.INSTANCE.registerPlayer(
							targetUser.getPlayerID(),
							password,
							new InetSocketAddress("0.0.0.0", 0).getAddress(),
							plugin
					);
				} catch (Exception ex) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fЧто-то обосралось при регистрации игрока");
					return;
				}

				log.info("[ACTION] Admin " + entity.getName() + " register player " + args[1]);
				entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fИгрок " + args[1] + " успешно зарегистрирован!");
			}

			case "changepass", "cp" -> {
				if (args.length < 2) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, используй: /auth cp <ник> <новый_пароль>");
					return;
				}

				val targetUser = AuthManager.INSTANCE.loadOrCreate(args[1]);
				if (targetUser == null) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, игрок " + args[1] + " не зарегистрирован!");
					return;
				}

				if (targetUser.getHandle().getGroup().getLevel() >= Group.ADMIN.getLevel()) {
					entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fОшибка, Вы не можете взаимодействовать с данным игроком.");
					return;
				}

				log.info("[ACTION] Admin " + entity.getName() + " change password for player " + args[1]);
				entity.sendMessage("§d§lАВТОРИЗАЦИЯ §8| §fИгрок " + args[1] + " успешно зарегистрирован");
				targetUser.setNewPassword(args[2]);
			}
		}
	}

	@Override
	public Iterable<String> tabComplete(final BungeeEntity entity, final String[] args) {
		if (args.length == 0) return ImmutableSet.of();

		Set<String> matches = new HashSet<>();
		if (args.length == 1) {
			matches.add("unregister");
			matches.add("license");
			matches.add("register");
			matches.add("changepass");

			return matches;
		}

		return ImmutableSet.of();
	}

}
