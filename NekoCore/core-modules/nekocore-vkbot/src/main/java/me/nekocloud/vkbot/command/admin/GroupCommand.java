package me.nekocloud.vkbot.command.admin;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.ValidateUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.common.group.GroupManager;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class GroupCommand extends VkCommand {

    public GroupCommand() {
        super("группа", "group");

        setGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyChats(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            sendHelpMessage(message, vkBot);
            return;
        }

        val manager = GroupManager.INSTANCE;
        switch (args[0].toLowerCase()) {
            case "список", "list" -> {
                val stringBuilder = new StringBuilder();
                stringBuilder.append("❗ Список доступных групп: \n");
                for (val group : Group.values()) {
                    stringBuilder.append(" - ")
                            .append(ChatColor.stripColor(group.getNameEn()))

                            .append(" (level:").append(group.getLevel())
                            .append(", name:").append(group.getGroupName())
                            .append(", id:").append(group.getId())
                            .append(")")

                            .append("\n");
                }
                new Message()
                        .peerId(message.getPeerId())
                        .body(stringBuilder.toString())
                        .send(vkBot);
            }
            case "получить", "getById" -> {
                if (args.length < 2) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: /group getById <ник игрока>");

                    break;
                }

                val playerGroup = GroupManager.INSTANCE.getPlayerGroup(args[1]);

                if (playerGroup == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, группа данного игрока не найдена!");

                    return;
                }

                vkBot.printMessage(message.getPeerId(), "❗ Группа игрока " + args[1] + " - " + ChatColor.stripColor(playerGroup.getNameEn()));
            }
            case "установить", "set" -> {
                if (args.length < 3) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: /group set <ник игрока> <группа (номер или имя)>");

                    break;
                }

                val currentPlayerName = args[1];

                // Так надо :( Это временно
                GlobalLoader.getPlayerID(args[2]);
                if (NekoCore.getInstance().getOfflinePlayer(currentPlayerName) == null) {
                    vkBot.printMessage(message.getPeerId(), "NO_NEVER_PAYER" + currentPlayerName);
                    return;
                }

                if (manager.getPlayerGroup(currentPlayerName).getLevel() >= Group.OWNER.getLevel()) {
                    vkBot.printMessage(message.getPeerId(), "Пососи))))");
                    return;
                }

                val groupToSet = ValidateUtil.isNumber(args[2]) ? Group.getGroupByLevel(Integer.parseInt(args[2])) : Group.getGroupByName(args[2]);

                if (groupToSet == null) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данной группы не существует!");

                    break;
                }

                if (groupToSet == Group.OWNER || groupToSet == Group.DEVELOPER) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, ты не можешь выдавать данную группу!");
                    return;
                }


                if (NekoCore.getInstance().getPlayer(currentPlayerName) != null) {
                    val corePlayer = NekoCore.getInstance().getPlayer(currentPlayerName);
                    GroupManager.INSTANCE.setGroupToPlayer(corePlayer, groupToSet);
                } else
                    GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, groupToSet);

                vkBot.printMessage(message.getPeerId(), "✅ Группа " + ChatColor.stripColor(groupToSet.getNameEn()) + " была выдана игроку " + ChatColor.stripColor(currentPlayerName));
            }
            case "перенос", "migarte", "merge" -> {
                if (args.length < 3) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: /group merge <ник игрока> <новый ник игрока>");

                    break;
                }

                val currentPlayerName = args[1];
                val targetPlayerName = args[2];

                if (manager.getPlayerGroup(currentPlayerName).getLevel() >= Group.OWNER.getLevel()) {
                    vkBot.printMessage(message.getPeerId(), "Пососи))))");
                    return;
                }

                val groupToMerge = manager.getPlayerGroup(currentPlayerName);
                val secondGroupToMerge = manager.getPlayerGroup(targetPlayerName);

                if (groupToMerge.getLevel() == 0) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ору, а какой смысл переносить группу игрока?");

                    break;
                }

                manager.setGroupToPlayer(currentPlayerName, secondGroupToMerge);
                manager.setGroupToPlayer(targetPlayerName, groupToMerge);

                vkBot.printMessage(message.getPeerId(), "✅ Группа " + ChatColor.stripColor(groupToMerge.getNameEn()) + " была перенесена игроку " + ChatColor.stripColor(targetPlayerName));
            }
            default -> sendHelpMessage(message, vkBot);
        }
    }

    private void sendHelpMessage(@NotNull Message message, VkBot vkBot) {
        vkBot.printMessage(message.getPeerId(),
                """
						❗ Список доступных команд:
						\s
						Установить группу игроку - /group set <ник игрока> <группа (уровень или имя)>
						Перенести группу игроку - /group merge <ник игрока> <новый ник игрока>
						Посмотреть список групп - /group list
						Узнать группу игрока - /group getById <ник игрока>
						\s
						""");
    }
}
