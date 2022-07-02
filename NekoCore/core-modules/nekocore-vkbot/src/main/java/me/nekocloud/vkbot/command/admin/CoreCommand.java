package me.nekocloud.vkbot.command.admin;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.DateUtil;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.ModuleManager;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.util.Collection;

public class CoreCommand extends VkCommand {

    public CoreCommand() {
        super("nekocore", "кор", "core");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            printStats(message, vkBot, vkUser);
            return;
        }

        val command = args[0];
        val serverManager = NekoCore.getInstance().getServerManager();
        switch (command) {
            case "stats", "info" -> printStats(message, vkBot, vkUser);
            case "server" -> {
                val abstractServer = serverManager.getBukkit(args[1]);

                if (abstractServer == null) {
                    vkBot.printMessage(message.getPeerId(), "Ошибка, данный сервер не существует или не подключен к Core!");
                    return;
                }

                val msg = "❗ Информация о сервере" + "\n" +
                        "- Название: " + abstractServer.getName() + ":" + "\n" +
                        "- MOTD: " + abstractServer.getName() + ":" + "\n" +
                        "- Директория: " + (abstractServer.getName().startsWith("bungee") ? "Bungee" : "Bukkit") + "\n" +
                        "- Версия ядра: nope" + "\n" +
                        "- Количество игроков: " + abstractServer.getOnline() + "\n";

                val corePlayer = NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName());
                if (!(corePlayer.getGroup().getLevel() >= Group.OWNER.getLevel())) {
                    vkBot.printMessage(message.getPeerId(), msg);
                    return;
                }

                new Message()
                        .peerId(message.getPeerId())
                        .body(msg)

                        .keyboard(true, true)
                        .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("/nekocore restart " + abstractServer.getName(), "Перезагрузить"))
                        .message()

                        .send(vkBot);
            }
            case "end" -> {
                if (checkOwnerPerms(vkUser)) return;
                vkBot.printMessage(message.getPeerId(), "NekoCore | Остановка Core....");
                me.nekocloud.core.common.commands.CoreCommand.alert("Остановка кора...");
                NekoCore.getInstance().shutdown();
            }

            case "restart" -> {
                if (checkOwnerPerms(vkUser)) return;
                if (args[1].equals("*")) {

                    for (val bukkitServer : serverManager.getBukkitServers().values()) {
                        bukkitServer.restart();
                    }

                    vkBot.printMessage(message.getPeerId(), "NekoCore | Все подключенные сервера были отправлены на перезагрузку!");
                    return;
                }

                if (args[1].startsWith("@") && args[1].length() > 1) {

                    Collection<Bukkit> servers = NekoCore.getInstance().getServersByPrefix(args[1].substring(1));
                    for (val bukkitServer : servers) {
                        bukkitServer.restart();
                    }

                    vkBot.printMessage(message.getPeerId(), "NekoCore | На перезагрузку было отправлено " + servers.size() + " серверов с префиксом " + args[1]);
                    return;
                }

                Bukkit bukkitServer = serverManager.getBukkit(args[1]);

                if (bukkitServer == null) {
                    vkBot.printMessage(message.getPeerId(), "NekoCore | Ошибка, сервер" + args[1] + " не подключен к Core!");
                    return;
                }

                vkBot.printMessage(message.getPeerId(),"NekoCore | Сервер " + bukkitServer.getName() + " успешно отправлен на перезагрузку");
                bukkitServer.restart();

            }
            case "mem" -> {
                val runtime = Runtime.getRuntime();
                val memString = "NekoCore | Статистика памяти:" + "\n" +
                        " ▪ Аптайм: " + TimeUtil.leftTime(Language.RUSSIAN,
                        System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) + "\n" +
                        " ▪ Максимально: " + runtime.maxMemory() / 1048576L + " МБ" + "\n" +
                        " ▪ Выделено: " + runtime.totalMemory() / 1048576L + " МБ" + "\n" +
                        " ▪ Свободно: " + runtime.freeMemory() / 1048576L + " МБ" + "\n" +
                        " ▪ Используется: " + (runtime.totalMemory() - runtime.freeMemory())
                        / 1048576L +
                        " МБ" + "\n";
                vkBot.printMessage(message.getPeerId(), memString);
            }

            case "langreload" -> {
                vkBot.printMessage(message.getPeerId(), "NekoCore | Перезагрузка локализации, загрузка новых значений...");
                try {
                    Language.reloadAll();
//                    NekoCore.getInstance().getServerManager()(new SLocaleUpdatePacket());
                } catch (Exception ex) {
                    vkBot.printMessage(message.getPeerId(), "NekoCore | При получении или отправке данных локализации произошла какая-то ошибка. " +
                            "Обратитесь к разработчику - github.com/novitpw");
                }

                vkBot.printMessage(message.getPeerId(), "NekoCore | Локализация успешно перезагружена!");
            }
            default -> printHelp(vkBot, message);
        }
    }

    private void printHelp(VkBot vkBot, Message message) {
        vkBot.printMessage(message.getPeerId(),
                """
                      NekoCore | Текущая версия: null
                      
                      /core stats - статистика Core
                      /core server <сервер(с номером)> - информация о сервере
                      /core restart <* / сервер / @префикс> - отправить на перезапуск все сервера/сервер
                      /core gc - выполнить GC
                      /core motdreload - перезагрузка MOTD
                      /core langreload - перезагрузка локализации
                      /core command <сервер> <команда> - отправить команду на сервер"""
        );
    }

    private void printStats(Message message, VkBot vkBot, VkUser vkUser) {
        val serverManager = NekoCore.getInstance().getServerManager();
        val stringBuilder = new StringBuilder();

        stringBuilder.append("NekoCore | Текущая версия: null").append("\n");
        stringBuilder.append("[СЕРВЕРА]:").append("\n");
        stringBuilder.append("- Bungee: ").append(serverManager.getBungeeServers().size()).append("\n");
        stringBuilder.append("- Bukkit: ").append(serverManager.getBukkitServers().size()).append("\n");

        stringBuilder.append("[ИГРОКИ]:").append("\n");
        stringBuilder.append("- Авторизированно: ").append(CoreAuth.getAuthManager().getAuthPlayerMap().size()).append("\n");
        stringBuilder.append("- Подключено: ").append(NekoCore.getInstance().getOnline()).append("\n");
//        stringBuilder.append("- Забанено: ").append(PunishmentManager.INSTANCE.getPunishmentMap().values().stream()
//                .filter(punishment -> punishment.isPermanent() && punishment.getPunishmentType().equals(PunishmentType.PERMANENT_BAN)).count()).append("\n");

        ModuleManager moduleManager = NekoCore.getInstance().getModuleManager();

        stringBuilder.append("[МОДУЛИ]: ").append("\n");
        stringBuilder.append("- Всего подключено: ").append(moduleManager.getModuleMap().size()).append("\n");
        stringBuilder.append("- Активно: ").append(moduleManager.getModuleMap().values().stream().filter(CoreModule::isEnabled).count()).append("\n");
        stringBuilder.append("- Команд зарегистрировано: ").append(NekoCore.getInstance().getCommandManager().getCommandMap().size()).append("\n");

//        stringBuilder.append("[ПРОТОКОЛ]:").append("\n");
//        stringBuilder.append("- TO_CLIENT: ").append(PacketProtocol..PLAY.TO_CLIENT.getIdToPackets().size()).append("\n");
//        stringBuilder.append("- TO_SERVER: ").append(Protocol.PLAY.TO_SERVER.getIdToPackets().size()).append("\n");
//        stringBuilder.append("- Всего пакетов: ").append(Protocol.PLAY.TO_SERVER.getIdToPackets().size() + Protocol.PLAY.TO_CLIENT.getIdToPackets().size()).append("\n");

        stringBuilder.append("- Аптайм: (").append(DateUtil.formatTime(NekoCore.getInstance().getStartSessionMillis(), DateUtil.DEFAULT_DATE_PATTERN))
                .append(") ").append(TimeUtil.leftTime(Language.DEFAULT, NekoCore.getInstance().getSessionMillis()));


        val corePlayer = NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName());
        if (!(corePlayer.getGroup().getLevel() >= Group.OWNER.getLevel())) {
            vkBot.printMessage(message.getPeerId(), stringBuilder.toString());
            return;
        }

        new Message()
                .peerId(message.getPeerId())
                .body(stringBuilder.toString())

                .keyboard(true, true)
                .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("/nekocloud end", "Перезапуск Core"))
                .button(KeyboardButtonColor.PRIMARY, 0, new TextButtonAction("/nekocore mem", "Статистика ОЗУ"))
                .message()

                .send(vkBot);
    }
}
