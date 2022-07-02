package me.nekocloud.core.discord.command.admin;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Collection;

public class ModuleCommand extends DiscordCommand {

    public ModuleCommand() {
        super("module", "модуль", "mod");

        setGroup(Group.ADMIN);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            sendHelpMessage(inputMessage);

            return;
        }

        switch (args[0].toLowerCase()) {
            case "включить":
            case "enable": {
                if (args.length < 2) {
                    inputMessage.reply("❗ Ошибка, используй: !module enable <название>").queue();
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    inputMessage.reply("❗ Ошибка, данный модуль не существует или не подключен!").queue();
                    break;
                }

                if (coreModule.isEnabled()) {
                    inputMessage.reply("❗ Ошибка, данный модуль уже запущен!").queue();
                    break;
                }

                coreModule.enableModule();
                inputMessage.reply("✅ Модуль " + coreModule.getName() + " успешно запущен!").queue();
                break;
            }

            case "выключить":
            case "disable": {
                if (args.length < 2) {
                    inputMessage.reply("❗ Ошибка в синтаксисе, пишите !module disable <название>").queue();
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    inputMessage.reply( "❗ Ошибка, данный модуль не существует или не подключен!").queue();
                    break;
                }

                if (!coreModule.isEnabled()) {
                    inputMessage.reply( "❗ Ошибка, данный модуль уже выключен!").queue();
                    break;
                }

                coreModule.disableModule();
                inputMessage.reply( "✅ Модуль " + coreModule.getName() + " успешно выключен!").queue();
                break;
            }

            case "перезагрузить":
            case "reload": {
                if (args.length < 2) {
                    inputMessage.reply( "❗ Ошибка, используй: пишите !module reload <название/*>").queue();
                    break;
                }

                if ("*".equals(args[1].toLowerCase())) {
                    Collection<CoreModule> coreModuleCollection
                            = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                    inputMessage.reply("❗ Все доступные модули кора были отправлены на перезагрузку!").queue();

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
                        inputMessage.reply("❗ Ошибка, данный модуль не существует или не подключен!").queue();
                        break;
                    }

                    inputMessage.reply("✅ Модуль " + coreModule.getName() + " отправлен на перезагрузку!").queue();

                    if (!coreModule.isEnabled()) {
                        coreModule.loadModule();

                        break;
                    }

                    coreModule.reloadModule();
                }

                break;
            }

            case "загрузить":
            case "load": {
                if (args.length < 2) {
                    inputMessage.reply("❗ Ошибка, используй: !module load <имя файла/*>").queue();
                    break;
                }

                if ("*".equals(args[1].toLowerCase())) {
                    Collection<CoreModule> coreModuleCollection
                            = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                    inputMessage.reply("❗ Все доступные модули кора были отправлены на загрузку!").queue();

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
                        inputMessage.reply("❗ Ошибка, файла " + fileName + " не существует в директории модулей!").queue();
                        break;
                    }

                    inputMessage.reply( "✅ Модуль " + fileName + " был успешно найден и загружен!").queue();

                    NekoCore.getInstance().getModuleManager()
                            .loadModuleFile(moduleFile);
                }

                break;
            }

            case "выгрузить":
            case "unload": {
                if (args.length < 2) {
                    inputMessage.reply("❗ Ошибка, используй: !module unload <название/*>").queue();
                    break;
                }

                if ("*".equals(args[1].toLowerCase())) {
                    Collection<CoreModule> coreModuleCollection
                            = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                    inputMessage.reply("Все доступные модули кора отправлены на отключение и выгрузку!").queue();

                    for (CoreModule coreModule : coreModuleCollection) {
                        coreModule.unloadModule();
                    }
                } else {
                    CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                            .getModule(args[1]);

                    if (coreModule == null) {
                        inputMessage.reply( "❗ Ошибка, данный модуль не существует или не подключен!").queue();
                        break;
                    }

                    inputMessage.reply("Модуль " + coreModule.getName() + " был отключен и выгружен!").queue();
                    coreModule.unloadModule();
                }

                break;
            }

            case "инфа":
            case "info": {
                if (args.length < 2) {
                    inputMessage.reply("❗ Ошибка, используй: !module info <название>").queue();
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    inputMessage.reply("❗ Ошибка, данный модуль не существует или не подключен!").queue();
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
                break;
            }

            case "список":
            case "list": {
                Collection<CoreModule> coreModuleCollection
                        = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                StringBuilder stringBuilder = new StringBuilder();
                for (CoreModule coreModule : coreModuleCollection) {

                    stringBuilder
                            .append("\n")
                            .append(coreModule.getName())
                            .append(coreModule.isEnabled() ? " [ON]" : " [OFF]");
                }

                inputMessage.reply("Список модулей кора (" + coreModuleCollection.size() + "): " + stringBuilder).queue();
                break;
            }

            default:
                sendHelpMessage(inputMessage);
        }
    }

    protected void sendHelpMessage(Message inputMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        {
            stringBuilder.append("❗ Список доступных команд:");
            stringBuilder.append("\n Загрузить модуля - !module load <имя файла/*>");
            stringBuilder.append("\n Отключить и выгрузить модуль - !module unload <название/*>");
            stringBuilder.append("\n Включить модуль - !module enable <название>");
            stringBuilder.append("\n Выключить модуль - !module disable <название>");
            stringBuilder.append("\n Перезагрузить все модули - !module reload *");
            stringBuilder.append("\n Информация о модуле - !module info <название>");
            stringBuilder.append("\n Показать список модулей - !module list");
        }

        inputMessage.reply(stringBuilder.toString()).queue();
    }
}
