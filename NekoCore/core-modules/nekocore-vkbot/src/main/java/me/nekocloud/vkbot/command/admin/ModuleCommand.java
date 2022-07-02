package me.nekocloud.vkbot.command.admin;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.module.CoreModule;

import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Collection;

public class ModuleCommand extends VkCommand {

    public ModuleCommand() {
        super("module", "модуль", "mod");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            sendHelpMessage(vkBot, message);

            return;
        }

        switch (args[0].toLowerCase()) {
            case "включить", "enable" -> {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: !module enable <название>");
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (coreModule.isEnabled()) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль уже запущен!");
                    break;
                }

                coreModule.enableModule();
                vkBot.printMessage(message.getPeerId(), "✅ Модуль " + coreModule.getName() + " успешно запущен!");
            }
            case "выключить", "disable" -> {
                if (checkOwnerPerms(vkUser)) return;
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, пишите !module disable <название>");
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (!coreModule.isEnabled()) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль уже выключен!");
                    break;
                }

                coreModule.disableModule();
                vkBot.printMessage(message.getPeerId(), "✅ Модуль " + coreModule.getName() + " успешно выключен!");
            }
            case "перезагрузить", "reload" -> {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: пишите !module reload <название/*>");
                    break;
                }

                if ("*".equals(args[1].toLowerCase())) {
                    Collection<CoreModule> coreModuleCollection
                            = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                    vkBot.printMessage(message.getPeerId(), "❗ Все доступные модули кора были отправлены на перезагрузку!");

                    for (CoreModule coreModule : coreModuleCollection) {

                        if (!coreModule.isEnabled()) {
                            coreModule.loadModule();

                            continue;
                        }

                        coreModule.reloadModule();
                    }
                } else {
                    CoreModule coreModule
                            = NekoCore.getInstance().getModuleManager().getModule(args[1]);

                    if (coreModule == null) {
                        vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                        break;
                    }

                    vkBot.printMessage(message.getPeerId(), "✅ Модуль " + coreModule.getName() + " отправлен на перезагрузку!");

                    if (!coreModule.isEnabled()) {
                        coreModule.loadModule();

                        break;
                    }

                    coreModule.reloadModule();
                }

            }
            case "загрузить", "load" -> {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: !module load <имя файла/*>");
                    break;
                }

                if ("*".equals(args[1].toLowerCase())) {
                    Collection<CoreModule> coreModuleCollection
                            = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                    vkBot.printMessage(message.getPeerId(), "❗ Все доступные модули кора были отправлены на загрузку!");

                    for (CoreModule coreModule : coreModuleCollection) {
                        coreModule.disableModule();
                    }

                    NekoCore.getInstance().getModuleManager().loadModules(
                            NekoCore.getInstance().getModulesFolder()
                    );
                } else {
                    String fileName = args[1].concat(".jar");
                    File moduleFile = new File(NekoCore.getInstance().getModulesFolder(), fileName);

                    if (!Files.exists(moduleFile.toPath())) {
                        vkBot.printMessage(message.getPeerId(), "❗ Ошибка, файла " + fileName + " не существует в директории модулей!");
                        break;
                    }

                    vkBot.printMessage(message.getPeerId(), "✅ Модуль " + fileName + " был успешно найден и загружен!");

                    NekoCore.getInstance().getModuleManager()
                            .loadModuleFile(moduleFile);
                }

            }
            case "выгрузить", "unload" -> {
                if (checkOwnerPerms(vkUser)) return;
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: !module unload <название/*>");
                    break;
                }

                if ("*".equals(args[1].toLowerCase())) {
                    Collection<CoreModule> coreModuleCollection
                            = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                    vkBot.printMessage(message.getPeerId(), "Все доступные модули кора отправлены на отключение и выгрузку!");

                    for (CoreModule coreModule : coreModuleCollection) {
                        coreModule.unloadModule();
                    }
                } else {
                    CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                            .getModule(args[1]);

                    if (coreModule == null) {
                        vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                        break;
                    }

                    vkBot.printMessage(message.getPeerId(), "Модуль " + coreModule.getName() + " был отключен и выгружен!");
                    coreModule.unloadModule();
                }

            }
            case "инфа", "info" -> {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: !module info <название>");
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный модуль не существует или не подключен!");
                    break;
                }

                StringBuilder stringBuilder = new StringBuilder();
                {
                    stringBuilder.append("Информация о модуле ").append(coreModule.getName()).append(":");
                    stringBuilder.append("\n Автор: ").append(coreModule.getAuthor());
                    stringBuilder.append("\n Зависимости: ").append(coreModule.getDepends().length == 0 ? "Пусто" : Joiner.on(" ").join(coreModule.getDepends()));
                    stringBuilder.append("\n Был запущен: ").append(new Timestamp(coreModule.getEnableMillis()).toGMTString()).append(" (").append(TimeUtil.leftTime(Language.DEFAULT, System.currentTimeMillis() - coreModule.getEnableMillis())).append(" назад)");
                    stringBuilder.append("\n Статус: ").append(coreModule.isEnabled() ? "включен" : "выключен");
                }

                new Message()
                        .peerId(message.getPeerId())
                        .body(stringBuilder.toString())

                        .keyboard(true, true)
                        .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("/module reload " + coreModule.getName(), "Перезагрузить модуль"))
                        .message()

                        .send(vkBot);
            }
            case "список", "list" -> {
                Collection<CoreModule> coreModuleCollection
                        = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                StringBuilder stringBuilder = new StringBuilder();
                for (CoreModule coreModule : coreModuleCollection) {

                    stringBuilder
                            .append("\n")
                            .append(coreModule.getName())
                            .append(coreModule.isEnabled() ? " [ON]" : " [OFF]");
                }

                vkBot.printMessage(message.getPeerId(), "Список модулей кора (" + coreModuleCollection.size() + "): " + stringBuilder);
            }
            default -> sendHelpMessage(vkBot, message);
        }
    }

    private void sendHelpMessage(VkBot vkBot, Message message) {
        vkBot.printMessage(message.getPeerId(),
                """
                        ❗ Список доступных команд:
                        
                        Загрузить модуля - !module load <имя файла/*>
                        Отключить и выгрузить модуль - !module unload <название/*>
                        Включить модуль - !module enable <название>
                        Выключить модуль - !module disable <название>
                        Перезагрузить все модули - !module reload *
                        Информация о модуле - !module info <название>
                        Показать список модулей - !module list
                        """);
    }
}
