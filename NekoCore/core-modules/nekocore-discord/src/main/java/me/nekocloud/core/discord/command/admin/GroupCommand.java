package me.nekocloud.core.discord.command.admin;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.ValidateUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.common.group.GroupManager;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class GroupCommand extends DiscordCommand {

    public GroupCommand() {
        super("группа", "group");

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
            case "list":
                val stringBuilder = new StringBuilder();
                stringBuilder.append("❗ Список доступных групп: \n");
                for (val group : Group.values()) {
                    stringBuilder.append(" - ")
                            .append(group.getNameEn())

                            .append(" (level:").append(group.getLevel())
                            .append(", name:").append(group.getGroupName())
                            .append(", id:").append(group.getId())
                            .append(")")

                            .append("\n");
                }
                inputMessage.reply(stringBuilder.toString()).queue();
                break;

            case "getById":
                if (args.length < 2) {
                    inputMessage.reply("❗ Ошибка, используй: /group getById <ник игрока>").queue();

                    break;
                }

                val playerGroup = GroupManager.INSTANCE.getPlayerGroup(args[1]);

                if (playerGroup == null) {
                    inputMessage.reply("❗ Ошибка, группа данного игрока не найдена!").queue();

                    return;
                }

                inputMessage.reply( "❗ Группа игрока " + args[1] + " - "  + playerGroup.getNameEn()).queue();
                break;

            case "set": {
                if (args.length < 3) {
                    inputMessage.reply("❗ Ошибка, используй: /group set <ник игрока> <группа (номер или имя)>").queue();

                    break;
                }

                val currentPlayerName = args[1];

                // Так надо :( Это временно
                GlobalLoader.getPlayerID(args[2]);
                if (NekoCore.getInstance().getOfflinePlayer(currentPlayerName) == null) {
                    inputMessage.reply("❗ Ошибка, игрок" + currentPlayerName + "никогда не играл на нашем проекте!").queue();
                    return;
                }

				if (currentPlayerName.equals("_Novit_") || currentPlayerName.equals("NezukoCo")) {
					inputMessage.reply("NekoCloud | Пососи))))").queue();
					return;
				}

                val groupToSet = ValidateUtil.isNumber(args[2]) ? Group.getGroupByLevel(Integer.parseInt(args[2])) : Group.getGroupByName(args[2]);

                if (groupToSet == null) {
                    inputMessage.reply("❗ Ошибка, данной группы не существует!").queue();

                    break;
                }

                if (groupToSet == Group.OWNER || groupToSet == Group.DEVELOPER) {
                    inputMessage.reply("❗ Ошибка, ты не можешь выдавать данную группу!").queue();
                    return;
                }


                if (NekoCore.getInstance().getPlayer(currentPlayerName) != null) {
                    val corePlayer = NekoCore.getInstance().getPlayer(currentPlayerName);
                    GroupManager.INSTANCE.setGroupToPlayer(corePlayer, groupToSet);
                } else
                    GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, groupToSet);

                inputMessage.reply("✅ Группа " + groupToSet.getNameEn() + " §fбыла выдана игроку §d" + currentPlayerName).queue();
                break;
            }

            case "migarte":
            case "merge":
                if (args.length < 3) {
                    inputMessage.reply("❗ Ошибка, используй: /group merge <ник игрока> <новый ник игрока>").queue();

                    break;
                }

                val currentPlayerName = args[1];
                val targetPlayerName = args[2];

				if (currentPlayerName.equals("_Novit_") || currentPlayerName.equals("NezukoCo")) {
					inputMessage.reply("Пососи))))").queue();
					return;
				}

                val groupToMerge = GroupManager.INSTANCE.getPlayerGroup(currentPlayerName);
                val secondGroupToMerge = GroupManager.INSTANCE.getPlayerGroup(targetPlayerName);

                if (groupToMerge.getLevel() == 0) {
                    inputMessage.reply("❗ Ору, а какой смысл переносить группу игрока?").queue();

                    break;
                }

                GroupManager.INSTANCE.setGroupToPlayer(currentPlayerName, secondGroupToMerge);
                GroupManager.INSTANCE.setGroupToPlayer(targetPlayerName, groupToMerge);

                inputMessage.reply("✅ Группа " + groupToMerge.getNameEn() + " была перенесена игроку §d" + targetPlayerName).queue();
                break;

            default:
                sendHelpMessage(inputMessage);
        }
    }

    private void sendHelpMessage(@NotNull Message inputMessage) {
        val builder = new StringBuilder();

        builder.append("❗ Список доступных команд:").append("\n");
        builder.append(" ").append("\n");
        builder.append("Установить группу игроку - /group set <ник игрока> <группа (уровень или имя)>").append("\n");
        builder.append("Перенести группу игроку - /group merge <ник игрока> <новый ник игрока>").append("\n");
        builder.append("Посмотреть список групп - /group list").append("\n");
        builder.append("Узнать группу игрока - /group getById <ник игрока>").append("\n");
        builder.append(" ").append("\n");

        inputMessage.reply(builder.toString()).queue();
    }
}
