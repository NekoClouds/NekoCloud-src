package me.nekocloud.core.common.group.command;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.util.ValidateUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.common.group.GroupManager;
import org.jetbrains.annotations.NotNull;

public class GroupCommand extends CommandExecutor {

    public GroupCommand() {
        super("group", "coregroup", "groups");

        setGroup(Group.ADMIN);
    }

    @Override
    protected void execute(
            final CommandSender entity,
            final String @NotNull[] args
    ) {
        if (args.length == 0) {
            sendHelpMessage(entity);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> {
                entity.sendMessage("§d§lГРУППЫ §8| §fСписок доступных групп:");
                val stringBuilder = new StringBuilder();
                for (val group : Group.values()) {
                    stringBuilder.append(" - ")
                            .append(group.getNameEn())

                            .append(" §7(level:").append(group.getLevel())
                            .append(", name:").append(group.getGroupName())
                            .append(", id:").append(group.getId())
                            .append("§7)")

                            .append("\n");
                }
                entity.sendMessage(stringBuilder.toString());
            }
            case "get" -> {
                if (args.length < 2) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОшибка, используй: §d/group get <ник игрока>");

                    break;
                }
                val playerGroup = GroupManager.INSTANCE.getPlayerGroup(args[1]);
                if (playerGroup == null) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОшибка, группа данного игрока не найдена!");

                    return;
                }
                entity.sendMessage("§d§lГРУППЫ §8| §fГруппа игрока " + args[1] + " - " + playerGroup.getNameEn());
            }
            case "set" -> {
                if (args.length < 3) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОшибка, используй: §5/group set <ник игрока> <группа (номер или имя)>");

                    break;
                }

                val currentPlayerName = args[1];

                if (NekoCore.getInstance().getOfflinePlayer(currentPlayerName) == null) {
                    entity.sendMessageLocale("NO_NEVER_PAYER", currentPlayerName);
                    return;
                }

                if (GroupManager.INSTANCE.getPlayerGroup(currentPlayerName).getLevel() >= Group.OWNER.getLevel()) {
                    entity.sendMessage("§d§lNekoCloud §8| §fПососи))))");
                    return;
                }

                val groupToSet = ValidateUtil.isNumber(args[2]) ? Group.getGroupByLevel(Integer.parseInt(args[2])) : Group.getGroupByName(args[2]);

                if (groupToSet == null) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОшибка, данной группы не существует!");

                    break;
                }

                if (groupToSet == Group.OWNER || groupToSet == Group.DEVELOPER) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОшибка, ты не можешь выдавать данную группу!");
                    return;
                }


                if (NekoCore.getInstance().getPlayer(currentPlayerName) != null) {
                    val corePlayer = NekoCore.getInstance().getPlayer(currentPlayerName);
                    GroupManager.INSTANCE.setGroupToPlayer(corePlayer, groupToSet);
                } else {
                    GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, groupToSet);
                }

                entity.sendMessage("§d§lГРУППЫ §8| §fГруппа " + groupToSet.getNameEn() + " §fбыла выдана игроку §d" + currentPlayerName);
            }
            case "migarte", "merge" -> {
                if (args.length < 3) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОшибка, используй: §d/group merge <ник игрока> <новый ник игрока>");

                    break;
                }
                val currentPlayerName = args[1];
                val targetPlayerName = args[2];

                if (GroupManager.INSTANCE.getPlayerGroup(currentPlayerName).getLevel() >= Group.OWNER.getLevel()) {
                    entity.sendMessage("§d§lNekoCloud §8| §fПососи))))");
                    return;
                }

                val groupToMerge = GroupManager.INSTANCE.getPlayerGroup(currentPlayerName);
                val secondGroupToMerge = GroupManager.INSTANCE.getPlayerGroup(targetPlayerName);
                if (groupToMerge.getLevel() == 0) {
                    entity.sendMessage("§d§lГРУППЫ §8| §fОру, а какой смысл переносить группу игрока?");
                    break;
                }

                GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, secondGroupToMerge);
                GroupManager.INSTANCE.setGroupToPlayer(targetPlayerName, groupToMerge);
                entity.sendMessage("§d§lГРУППЫ §8| §fГруппа " + groupToMerge.getNameEn() + " §fбыла перенесена игроку §d" + targetPlayerName);
            }
            default -> sendHelpMessage(entity);
        }
    }

    private void sendHelpMessage(final @NotNull CommandSender entity) {
        entity.sendMessage("""
        §d§lГРУППЫ §8| §fСписок доступных команд:\s
        
        §d     Установить группу игроку §8- §f/group set <ник игрока> <группа (уровень или имя)>
        §d     Перенести группу игроку §8- §f/group merge <ник игрока> <новый ник игрока>
        §d     Посмотреть список групп §8- §f/group list
        §d     Узнать группу игрока §8- §f/group get <ник игрока>\s""");
    }

}
