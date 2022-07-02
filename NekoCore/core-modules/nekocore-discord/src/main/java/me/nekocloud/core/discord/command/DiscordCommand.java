package me.nekocloud.core.discord.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.common.group.GroupManager;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public abstract class DiscordCommand {

    public List<String> aliases = new ArrayList<>();
    public NekoCore core        = NekoCore.getInstance();

    @Setter boolean shouldLinkAccount = false;
    @Setter boolean onlyPrivateMessages = false;
    @Setter boolean onlyGuild = false;

    @Setter long guildChatId = 0;
    @Setter Group group = Group.PIDOR;

    public DiscordCommand(String command, String... aliases){
        this.aliases.add(command);

        if (aliases != null) this.aliases.addAll(Arrays.asList(aliases));
    }

    protected abstract void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel);

    public void executeCommand(String[] args, User author, Message inputMessage, MessageChannel channel) {
        try {
            val discordUser = DiscordUser.getDiscordUser(author.getIdLong());

            if (isOnlyPrivateMessages() && inputMessage.isFromGuild()) {
                channel.sendMessage(author.getAsMention() + " ❗ Ошибка, для использования данной команды " +
                        "Тебе необходимо перейти в личные сообщение бота").queue();
                inputMessage.delete().queue();

                return;
            }

            if (isShouldLinkAccount() && !discordUser.hasPrimaryAccount()) {
                inputMessage.reply("❗ Ошибка, Ты не имеешь привязанного аккаунта Discord!\n" +
                        "\n\uD83D\uDCE2Введи !привязать <ник> <пароль>, чтобы привязать аккаунт!").queue();
                return;
            }

            if (discordUser.hasPrimaryAccount()) {
                val playerGroup = GroupManager.INSTANCE.getPlayerGroup(
                        discordUser.getPrimaryAccountName());

                val minLevel = group.getLevel();
                if (playerGroup.getLevel() < minLevel) {
                    if (minLevel == Group.ADMIN.getLevel() || group == Group.SRMODERATOR || group == Group.MODERATOR) {
                        inputMessage.reply("❗ Ошибка, у тебя недостаточно прав!").queue();
                    } else {
                        inputMessage.reply("❗ Ошибка, для выполнения данной команды необходим статус "
                                + ChatColor.stripColor(group.getNameEn()) + " и выше!").queue();
                    }
                    return;
                }
            }

            execute(args, author, discordUser, inputMessage, channel);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static String trimForNoobs(@NotNull String commandLine) {
        return commandLine.replace("<", "").replace(">", "");
    }
}
