package me.nekocloud.nekoapi.commands;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Спиздил у стонлекса
 */
public final class ManagerCommand implements CommandInterface {

	public ManagerCommand() {
		val command = COMMANDS_API.register("pmanager", this,
				"plugman", "plug", "plugin");
		command.setGroup(Group.OWNER);
	}

	@Override
	public void execute(final GamerEntity gamerEntity, final String command, final String @NotNull [] args) {
		if (args.length < 1) {
			printHelpMessages(gamerEntity);
			return;
		}

		val pluginManager = Bukkit.getPluginManager();
		switch (args[0].toLowerCase()) {
			case "list", "список" -> gamerEntity.sendMessage("§d§lNekoAPI §8| §fСписок плагинов (" +
					pluginManager.getPlugins().length + "): " +
					Joiner.on("§f, ").join(Arrays.stream(pluginManager.getPlugins()).map(plugin ->
						(plugin.isEnabled() ? "§a" : "§c") + plugin.getName()).collect(Collectors.toSet()))
			);
			case "info", "stats" -> {
				if (args.length < 2) {
					COMMANDS_API.notEnoughArguments(gamerEntity, "/pmanager info <плагин>");
					return;
				}

				val plugin = Bukkit.getPluginManager().getPlugin(args[1]);
				if (plugin == null) {
					gamerEntity.sendMessage("§cОшибка, плагина " + args[1] + " не сущесвтует или не подгружен ядром!");
					return;
				}

				gamerEntity.sendMessage("§d§lNekoAPI §8| §fСтатистика о плагине §e" + plugin.getName());
				gamerEntity.sendMessage(" §7Статус: " + (plugin.isEnabled() ? "§aвключен" : "§cвыключен"));
				gamerEntity.sendMessage(" §7Директория: " + (plugin.getDataFolder().exists() ? "§aесть" : "§cнет"));
				gamerEntity.sendMessage(" §7Конфигурация: " + (plugin.getDataFolder().toPath().resolve("config.yml").toFile().exists() ? "§aесть" : "§cнет"));
				gamerEntity.sendMessage("\n");

				val pluginDescription = plugin.getDescription();
				gamerEntity.sendMessage(" §fОписание:");
				gamerEntity.sendMessage("  §7Название: §e" + pluginDescription.getName());
				gamerEntity.sendMessage("  §7Версия: §e" + pluginDescription.getVersion());
				gamerEntity.sendMessage("  §7Главный класс: §e" + pluginDescription.getMain());

				if (!pluginDescription.getAuthors().isEmpty()) {
					if (pluginDescription.getAuthors().size() < 2)
						gamerEntity.sendMessage("  §7Автор: §e" + pluginDescription.getAuthors()
								.stream().findFirst().orElse("<нет>"));
					else
						gamerEntity.sendMessage("  §7Авторы: §e" + Joiner.on("§7, §e")
								.join(pluginDescription.getAuthors()));
				}

				if (pluginDescription.getWebsite() != null)
					gamerEntity.sendMessage("  §7Сайт: §e" + pluginDescription.getWebsite());

				if (pluginDescription.getDepend() != null && !pluginDescription.getDepend().isEmpty())
					gamerEntity.sendMessage("  §7Зависимости: §a" + Joiner.on("§7, §a").join(pluginDescription.getDepend()));
			}

			case "enable", "on" -> {
				if (args.length < 2) {
					COMMANDS_API.notEnoughArguments(gamerEntity, "/pmanager enable <плагин>");
					return;
				}

				val plugin = Bukkit.getPluginManager().getPlugin(args[1]);
				if (plugin == null) {
					gamerEntity.sendMessage("§cОшибка, плагина " + args[1] + " не сущесвтует или не подгружен ядром!");
					return;
				}

				if (plugin.isEnabled()) {
					gamerEntity.sendMessage("§cОшибка, плагин " + plugin.getName() + " уже запущен!");
					return;
				}

				Bukkit.getPluginManager().enablePlugin(plugin);
				gamerEntity.sendMessage("§d§lNekoAPI §8| §fПлагин §b" + plugin.getName() + " §fуспешно запущен!");
			}
			case "disable", "off" -> {
				if (args.length < 2) {
					COMMANDS_API.notEnoughArguments(gamerEntity, "/pmanager disable <плагин>");
					return;
				}

				val plugin = Bukkit.getPluginManager().getPlugin(args[1]);
				if (plugin == null) {
					gamerEntity.sendMessage("§cОшибка, плагина " + args[1] + " не сущесвтует или не подгружен ядром!");
					return;
				}

				if (!plugin.isEnabled()) {
					gamerEntity.sendMessage("§cОшибка, плагин " + plugin.getName() + " уже выключен!");
					return;
				}

				Bukkit.getPluginManager().disablePlugin(plugin);
				gamerEntity.sendMessage("§d§lNekoAPI §8| §fПлагин §e" + plugin.getName() + " §fуспешно выключен!");
			}
			case "reload", "restart" -> {
				if (args.length < 2) {
					COMMANDS_API.notEnoughArguments(gamerEntity, "/pmanager reload <плагин>");
					return;
				}

				val plugin = Bukkit.getPluginManager().getPlugin(args[1]);
				if (plugin == null) {
					gamerEntity.sendMessage("§cОшибка, плагина " + args[1] + " не сущесвтует или не подгружен ядром!");
					return;
				}

				if (!plugin.isEnabled()) {
					Bukkit.getPluginManager().enablePlugin(plugin);

				} else {

					Bukkit.getPluginManager().disablePlugin(plugin);
					Bukkit.getPluginManager().enablePlugin(plugin);
				}

				gamerEntity.sendMessage("§d§lNekoAPI §8| §fПлагин §e" + plugin.getName() + " §fуспешно перезапущен!");
			}
			default -> printHelpMessages(gamerEntity);
		}
	}

	private void printHelpMessages(final GamerEntity entity) {
		entity.sendMessage("§d§lNekoAPI §8| §fСписок доступных команд:");
		entity.sendMessage(" §fСписок плагинов - §d/pmanager list");
		entity.sendMessage(" §fИнформация о плагине - §d/pmanager info <плагин>");
		entity.sendMessage(" §fВыключить плагин - §d/pmanager disable/off <плагин>");
		entity.sendMessage(" §fВключить плагин - §d/pmanager enable/on <плагин>");
		entity.sendMessage(" §fПерезапустить плагин - §d/pmanager reload <плагин>");
	}
}
