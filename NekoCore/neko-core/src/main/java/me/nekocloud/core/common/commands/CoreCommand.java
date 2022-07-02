package me.nekocloud.core.common.commands;

import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.DateUtil;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.JsonChatMessage;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.chat.event.ClickEvent;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.io.packet.PacketProtocol;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.util.Collection;

@Log4j2
public class CoreCommand extends CommandExecutor {

    public CoreCommand() {
        super("core", "ncore", "nekocore");

        setGroup(Group.ADMIN);
    }

    @Override
    protected void execute(
            final @NotNull CommandSender sender,
            final String @NotNull[] args
    ) {
        val serverManager = NekoCore.getInstance().getServerManager();

        if (args.length == 0) {
            sender.sendMessageLocale("CORE_HELP", NekoCore.getInstance());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "cmd", "command" -> {
                val bukkit = serverManager.getBukkit(args[1]);

                val commandLine = args[2];
                val split = commandLine.split(" ");
                val command = split[0].toLowerCase();

                if (bukkit == null) {
                    sender.sendMessageLocale("NOT_SERVER", args[0]);
                    return;
                }

                bukkit.sendCommand(Joiner.on("").join(split));
                sender.sendMessage("Команда успешно отправлена на сервер " + bukkit.getName());

            }
            case "debug" -> {

            }
            case "motdreload" -> {
            }

            case "langreload" -> {
                // TODO: ПЕРЕПИСАТЬ ЭТУ ХУЙНЮ!! отправлять в пакете саму локали, а не загружать нахуй с 10 серверов
                sender.sendMessage("§dNekoCore §8| §fПерезагрузка локализации, загрузка новых значений...");
                try {
                    Language.reloadAll();
                    for (val bukkit : NekoCore.getInstance().getBukkitServers())
                        bukkit.langReload();

                } catch (Exception ex) {
                    sender.sendMessage("§dNekoCore §8| §fПри получении или отправке данных локализации произошла какая-то ошибка. " +
                            "Обратитесь к разработчику - §dgithub.com/novitpw");
                    break;
                }

                sender.sendMessage("§dNekoCore §8| §fЛокализация успешно перезагружена!");
            }
            case "mem" -> {
                val runtime = Runtime.getRuntime();
                sender.sendMessage("§dNekoCore §8| §fСтатистика памяти§f:");
                sender.sendMessage(" §c▪ §fАптайм: §7"
                        + TimeUtil.leftTime(Language.RUSSIAN,
                        System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()));
                sender.sendMessage(" §c▪ §fМаксимально: §7" + runtime.maxMemory() / 1048576L + " МБ");
                sender.sendMessage(" §c▪ §fВыделено: §a" + runtime.totalMemory() / 1048576L + " МБ");
                sender.sendMessage(" §c▪ §fСвободно: §e" + runtime.freeMemory() / 1048576L + " МБ");
                sender.sendMessage(" §c▪ §fИспользуется: §c" + (runtime.totalMemory() - runtime.freeMemory())
                        / 1048576L + " МБ");
                sender.sendMessage(" ");
            }

            case "gc" -> {
                System.gc();
                sender.sendMessage("Кэш Core очищен");
            }

            case "stats" -> {
                val moduleManager = NekoCore.getInstance().getModuleManager();
                sender.sendMessagesLocale("CORE_STATS",
                        serverManager.getBungeeServers().size(),
                        serverManager.getBukkitServers().size(),
                        NekoCore.getInstance().getOnline(),
                        CoreAuth.getAuthManager().getAuthPlayerMap().size(),
                        moduleManager.getModuleMap().size(),
                        moduleManager.getModuleMap().values().stream().filter(CoreModule::isEnabled).count(),
                        NekoCore.getInstance().getCommandManager().getCommandMap().size(),
                        PacketProtocol.BUKKIT,
                        DateUtil.formatTime(NekoCore.getInstance().getStartSessionMillis(), DateUtil.DEFAULT_DATE_PATTERN),
                        TimeUtil.leftTime(sender.getLanguage(), NekoCore.getInstance().getSessionMillis()));
            }
            case "stop" -> {
                alert("Остановка кора...");
                NekoCore.getInstance().shutdown();
            }

            case "restart" -> {
                if (args[1].equals("*")) {

                    for (val bukkitServer : serverManager.getBukkitServers().values()) {
                        bukkitServer.restart();
                    }

                    sender.sendMessage("§d§lNekoCore §8| §fВсе подключенные сервера Bukkit были отправлены на перезагрузку!");
                    break;
                }

                if (args[1].startsWith("@") && args[1].length() > 1) {

                    Collection<Bukkit> servers = NekoCore.getInstance().getServersByPrefix(args[1].substring(1));
                    for (val bukkitServer : servers) {
                        bukkitServer.restart();
                    }

                    sender.sendMessage("§d§lNekoCore §8| §fНа перезагрузку было отправлено §5" + servers.size() + " §fсерверов с префиксом §e" + args[1]);
                    break;
                }

                Bukkit bukkit = serverManager.getBukkit(args[1]);
                if (bukkit == null) {
                    sender.sendMessageLocale("NOT_SERVER", args[1]);
                    return;
                }

                sender.sendMessage("§d§lNekoCore §8| §fСервер §e" + bukkit.getName() + " §fуспешно отправлен на перезагрузку");
                //abstractServer.restart();
            }
            case "srv", "serv", "server" -> {
                Bukkit bukkitServer = serverManager.getBukkit(args[1]);
                if (bukkitServer == null) {
                    sender.sendMessageLocale("NOT_SERVER", args[1]);
                    return;
                }

                sender.sendMessageLocale("CORE_INFO_SERVER",
                        bukkitServer.getName(),
                        bukkitServer.getName(),
                        ("BUKKIT"),
                        Version.getVersion(bukkitServer.getServerInfo().getProtocolVersion()).toClientName(),
                        bukkitServer.getOnline(),
                        bukkitServer.getAddress().getAddress().getHostAddress()
                );

                val lang = sender.getLanguage();

                // Создаем кнопки
                val restartButton = JsonChatMessage.create(lang.getMessage("BUTTON_RESTART"))
                        .addHover(HoverEvent.Action.SHOW_TEXT, lang.getMessage("CLICK_TO_RESTART_SERVER"))
                        .addClick(ClickEvent.Action.RUN_COMMAND, "/core restart " + bukkitServer.getName())
                        .build();

                val tpButton = JsonChatMessage.create(lang.getMessage("BUTTON_TELEPORT"))
                        .addHover(HoverEvent.Action.SHOW_TEXT, lang.getMessage("CLICK_TO_TELEPORT_SERVER"))
                        .addClick(ClickEvent.Action.RUN_COMMAND, "/server " + bukkitServer.getName())
                        .build();

                JsonChatMessage.create()

                        .addText("\n")

                        .addComponents(TextComponent.fromLegacyText(" "))
                        .addComponents(tpButton)

                        .addComponents(TextComponent.fromLegacyText("        "))
                        .addComponents(restartButton)

                        .sendMessage(sender);
            }
            default -> sender.sendMessageLocale("CORE_HELP");
        }
    }

    public static void alert(@NotNull String message) {
        message = ("§5§lОБЪЯВЛЕНИЕ §8➾ §f ") + message;

        for (val player : NekoCore.getInstance().getOnlinePlayers()) {
            player.sendMessage(message);
        }

        log.info(message);
    }
}
