package me.nekocloud.vkbot.command;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends VkCommand {

    public HelpCommand() {
        super("помощь", "команды", "комманды", "commands", "кмд");
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        val builder = new StringBuilder();

        if (args.length < 1) {
            printCommandsMessage(vkUser, builder, vkBot, message);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "акк", "аккаунт", "инфо" -> {
                builder.append("❗ Использование команды - !аккаунт <ник>").append("\n")
                        .append("- Узнать информацию об аккаунте игрока");

                sendMessage(vkBot, builder, message);
            }
            case "link", "привязать" -> {
                builder.append("❗ Использование команды - !привязать <ник> <пароль>").append("\n")
                        .append("- Привязать игровой аккаунт в VK");
                if (message.isFromChat())
                    builder.append("\n\n")
                            .append("⚠ Команда доступна только в ЛС бота!");

                sendMessage(vkBot, builder, message);
            }
            case "unlink", "отвязать" -> {
                builder.append("❗ Использование команды - !отвязать <ник>").append("\n")
                        .append("- Отвязать аккаунт от VK");
                if (message.isFromChat())
                    builder.append("\n\n")
                            .append("⚠ Команда доступна только в ЛС бота!");

                sendMessage(vkBot, builder, message);
            }
            case "recovery", "восстановить" -> {
                builder.append("❗ Использование команды - !восстановить <ник>").append("\n")
                        .append("- Сбросить пароль от игрового аккаунта");
                if (message.isFromChat())
                    builder.append("\n\n")
                            .append("⚠ Команда доступна только в ЛС бота!");

                sendMessage(vkBot, builder, message);
            }
            default -> {
                printCommandsMessage(vkUser, builder, vkBot, message);
            }
        }
    }

    private void sendMessage(VkBot vkBot, StringBuilder builder, Message message) {
            new Message()
                .peerId(message.getPeerId())
                .body(builder.toString())
                .send(vkBot);
    }


    private void printCommandsMessage(VkUser user, StringBuilder builder, VkBot vkBot, Message message) {
        builder.append("""
                ❗ Список доступных команд:\s
                \s
                ⚠ Узнать информацию об аккаунте - !аккаунт\s
                ⚠ Узнать ник пользователя - !ник\s
                \s
                \uD83D\uDCCC Привязать аккаунт - !привязать <ник> <пароль>\s
                \uD83D\uDCCC Отвязать аккаунт - !отвязать <ник>\s
                \uD83D\uDCCC Сменить/восстановить пароль - !восстановить\s
                \s
                \uD83D\uDC40 Узнать онлайн общего или определённого сервера - !онлайн <сервер>\s
                \uD83D\uDC40 Найти игрока на сервере - !найти <ник>""");
        if (user.hasPrimaryAccount() && NekoCore.getInstance().getOfflinePlayer(user.getPrimaryAccountName()).isStaff()) {
            builder.append("""


                    ❗ Список команд для модераторов проекта:\s
                    \s
                    \uD83C\uDD95 Кикнуть игрока с сервера - !кик <ник> <причина>\s
                    """);
        if (user.hasPrimaryAccount() && NekoCore.getInstance().getOfflinePlayer(user.getPrimaryAccountName()).isAdmin()) {
            builder.append("""


                    ❗ Список команд для администрации:\s
                    \s
                    \uD83C\uDD95 Выдать группу игроку - !группа <игрок> <название/номер>\s
                    \uD83C\uDD95 Перенести данные игрока на другой аккаунт -\s
                    !перенос <игрок 1> <игрок 2>\s
                    \s
                    \uD83C\uDF6D Посмотреть информацию о подключенном сервере - !сервер <название>\s
                    \uD83C\uDF6D Удаленно перезагрузить сервер - !рестарт <название>\s
                    \uD83C\uDF6D Объявить сообщение на весь сервер - !alert <сообщение>\s
                    \uD83C\uDF6D Объявить сообщение на весь сервер - !ip <ник>\s
                    \uD83C\uDF6D Управление модулями - !модуль""");
            }
        }
        sendMessage(vkBot, builder, message);
    }
}
