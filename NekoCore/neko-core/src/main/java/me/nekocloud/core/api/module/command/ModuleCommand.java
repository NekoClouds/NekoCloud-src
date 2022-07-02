package me.nekocloud.core.api.module.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.util.DateUtil;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.module.CoreModule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

public class ModuleCommand extends CommandExecutor {

    public ModuleCommand() {
        super("module","модуль", "mod");

        setOnlyAuthorized(true);

        setGroup(Group.ADMIN);
    }

    @Override
    protected void execute(final @NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);

            return;
        }

        switch (args[0].toLowerCase()) {
            case "enable" -> {
                if (args.length < 2) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, используй: /module enable <название>");
                    break;
                }

                val coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (coreModule.isEnabled()) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, данный модуль уже запущен!");
                    break;
                }

                coreModule.enableModule();
                sender.sendMessage("§dNekoCore §8| §fМодуль §e" + coreModule.getName() + " §fуспешно запущен!");
            }
            case "disable" -> {
                if (args.length < 2) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, используй: /module disable <название>");
                    break;
                }

                val coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, данный модуль не существует или не подключен!");
                    break;
                }

                if (!coreModule.isEnabled()) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, данный модуль уже выключен!");
                    break;
                }

                coreModule.disableModule();
                sender.sendMessage("§dNekoCore §8| §fМодуль §e" + coreModule.getName() + " §fуспешно выключен!");
            }
            case "reload" -> {
                if (args.length < 2) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, используй: /module reload <название/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {
                    case "*" -> {
                        Collection<CoreModule> coreModuleCollection
                                = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.reloadModule();
                        }

                        sender.sendMessage("§dNekoCore §8| §fВсе доступные модули кора были успешно перезагружены!");
                    }
                    default -> {
                        val coreModule
                                = NekoCore.getInstance().getModuleManager().getModule(args[1]);

                        if (coreModule == null) {
                            sender.sendMessage("vОшибка, данный модуль не существует или не подключен!");
                            break;
                        }

                        coreModule.reloadModule();
                        sender.sendMessage("§dNekoCore §8| §fМодуль §e" + coreModule.getName() + " §fбыл перезагружен!");
                    }
                }

            }
            case "load" -> {
                if (args.length < 2) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, используй: /module load <имя файла/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {
                    case "*" -> {
                        Collection<CoreModule> coreModuleCollection
                                = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                        sender.sendMessage("§dNekoCore §8| §fВсе доступные модули кора были отправлены на загрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.unloadModule();
                        }

                        NekoCore.getInstance().getModuleManager().loadModules(NekoCore.getInstance().getModulesFolder());
                    }
                    default -> {
                        val fileName = args[1].concat(".jar");
                        val moduleFile = new File(NekoCore.getInstance().getModulesFolder(), fileName);

                        if (!Files.exists(moduleFile.toPath())) {
                            sender.sendMessage("§dNekoCore §8| §fОшибка, файла " + fileName + " не существует в директории модулей!");
                            break;
                        }

                        NekoCore.getInstance().getModuleManager()
                                .loadModuleFile(moduleFile);

                        sender.sendMessage("§dNekoCore §8| §fJAR модуль " + fileName + " §fбыл успешно найден и загружен!");
                    }
                }

            }
            case "unload" -> {
                if (args.length < 2) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, используй: /module unload <название/*>");
                    break;
                }

                switch (args[1].toLowerCase()) {
                    case "*" -> {
                        Collection<CoreModule> coreModuleCollection
                                = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                        sender.sendMessage("§dNekoCore §8| §fВсе доступные модули кора отправлены на выгрузку!");

                        for (CoreModule coreModule : coreModuleCollection) {
                            coreModule.unloadModule();
                        }

                    }
                    default -> {
                        CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                                .getModule(args[1]);

                        if (coreModule == null) {
                            sender.sendMessage("§cОшибка, данный модуль не существует или не подключен!");
                            break;
                        }

                        coreModule.unloadModule();
                        sender.sendMessage("§dNekoCore §8| §fМодуль " + coreModule.getName() + " §fбыл отключен и выгружен");
                    }
                }

            }
            case "info" -> {
                if (args.length < 2) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, используй: /module info <название>");
                    break;
                }

                CoreModule coreModule = NekoCore.getInstance().getModuleManager()
                        .getModule(args[1]);

                if (coreModule == null) {
                    sender.sendMessage("§dNekoCore §8| §fОшибка, данный модуль не существует или не подключен!");
                    break;
                }

                sender.sendMessage("§dNekoCore §8| §fИнформация о модуле §e" + coreModule.getName() + "§f:");
                sender.sendMessage(" §7Авторы: §e" + Arrays.toString(coreModule.getAuthors()));
                sender.sendMessage(" §7Зависимости: §e" + (coreModule.getDepends().length == 0 ? "§cнет" : Joiner.on("§f, §a").join(coreModule.getDepends())));
                sender.sendMessage(" §7Был запущен: §e" + DateUtil.formatTime(coreModule.getEnableMillis(), DateUtil.DEFAULT_DATETIME_PATTERN) + " §7(" + TimeUtil.leftTime(sender.getLanguage(), System.currentTimeMillis() - coreModule.getEnableMillis()) + " назад)");
                sender.sendMessage(" §7Статус: " + (coreModule.isEnabled() ? "§aвключен" : "§cвыключен"));
            }
            case "list" -> {
                Collection<CoreModule> coreModuleCollection
                        = NekoCore.getInstance().getModuleManager().getModuleMap().values();

                StringBuilder stringBuilder = new StringBuilder();
                for (val coreModule : coreModuleCollection) {

                    stringBuilder.append(coreModule.isEnabled() ? ChatColor.GREEN : ChatColor.RED)
                            .append(coreModule.getName()).append("§f, ");
                }

                sender.sendMessage("§dNekoCore §8| §fСписок модулей (" + coreModuleCollection.size() + "): §e" + stringBuilder.substring(0, stringBuilder.toString().length() - 4));
            }
            default -> {
                sender.sendMessage("§dNekoCore §8| §fОшибка, данный аргумент не существует!");
                sendHelpMessage(sender);
            }
        }
    }

    protected void sendHelpMessage(final @NotNull CommandSender entity) {
        entity.sendMessage("§dNekoCore §8| §fСписок доступных команд:");

        entity.sendMessage("     §dЗагрузить модуль §8- §7/module load <имя файла/*>");
        entity.sendMessage("     §dОтключить и выгрузить модуль §8- §7/module unload <название/*>");
        entity.sendMessage("     §dПерезагрузить модуль §8- §7/module reload <название/*>");

        entity.sendMessage("     §dВключить модуль §8- §7/module enable <название>");
        entity.sendMessage("     §dОтключить модуль §8- §7/module disable <название>");

        entity.sendMessage("     §dИнформация о модуле §8- §7/module info <название>");
        entity.sendMessage("     §dПоказать список модулей §8- §7/module list");
    }

}
