package me.nekocloud.vkbot.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.common.group.GroupManager;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public abstract class VkCommand {

    final String[] aliases;

    public VkCommand(String... aliases) {
        Preconditions.checkArgument(aliases.length > 0, "You must specify command name as a first argument in constructor");

        this.aliases = aliases;
    }

    /**
     * Будет ли команда доступна только тем, у кого
     * установлен основной аккаунт в боте
     */
    @Setter
    boolean shouldLinkAccount = false;

    /**
     * Доступна ли команда для использования только в ЛС
     * основной группы
     */
    @Setter
    boolean onlyPrivateMessages = false;

    /**
     * При вводе команды в чате, она не
     * буде выполнятся.
     */
    @Setter
    boolean listenOnChats = true;

    /**
     * Команда доступна только в беседах
     */
    @Setter
    boolean onlyChats = false;

    /**
     * Минимальный статус доступа к команде
     */
    @Setter
    Group group = Group.DEFAULT;

    VkBot vkBot;
    Message message;

    public void dispatchCommand(@NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        this.vkBot = vkBot;
        this.message = message;
        try {
            val botUser = VkUser.getVkUser(message.isFromChat() ? message.getUserId() : message.getPeerId());

            if (!(isListenOnChats()) && message.isFromChat())
                return;

            if (isOnlyPrivateMessages() && message.isFromChat()) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, для использования данной команды Вам необходимо перейти в личные сообщение бота: https://vk.me/nekocloud");

                return;
            }

            if (isShouldLinkAccount() && !botUser.hasPrimaryAccount()) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, Вы не имеете привязанного аккаунта к VK!\n" +
                        "\n\uD83D\uDCE2Введите !привязать <ваш ник> <пароль>, чтобы привязать аккаунт!");
                return;
            }


            if (botUser.hasPrimaryAccount()) {
                //Группа игрока
                val playerGroup = GroupManager.INSTANCE.getPlayerGroup(botUser.getPrimaryAccountName());

                int minLevel = group.getLevel();
                if (playerGroup.getLevel() < minLevel) {
                    if (minLevel == Group.ADMIN.getLevel() || group == Group.SRMODERATOR || group == Group.MODERATOR) {
                        vkBot.printMessage(message.getPeerId(),"❗ Ошибка, у вас недостаточно прав!");
                    } else {
                        vkBot.printMessage(message.getPeerId(),"❗ Ошибка, для выполнения данной команды необходим статус " + group.getGroupName().toUpperCase() + " и выше!");
                    }
                    return;
                }
            }

            execute(botUser, message, args, vkBot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Message getFirstForwardedMessage(@NotNull Message message) {
        List<Message> forwarded = message.getForwardedMessages();

        return forwarded.isEmpty() ? null : forwarded.get(0);
    }

    protected abstract void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot);

    protected static String trimForNoobs(@NotNull String commandLine) {
        return commandLine.replace("<", "").replace(">", "");
    }

    protected boolean checkOwnerPerms(VkUser vkUser) {
        val corePlayer = NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName());
        if (!(corePlayer.getGroup().getLevel() >= Group.OWNER.getLevel())) {
            vkBot.printMessage(message.getPeerId(),
                    "❗ Ошибка, для выполнения данной команды необходим статус "
                            + Group.OWNER.getGroupName().toUpperCase() + " и выше!");
            return true;
        }
        return false;
    }

    protected boolean hasIdentifier(@NotNull String name) {
        if (!NekoCore.getInstance().getNetworkManager().hasIdentifier(name)) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок никогда не играл на сервере!");
            return false;
        }
        return true;
    }

    protected boolean hasPerms(VkUser vkUser, Group group) {
        val corePlayer = NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName());
        if (!(corePlayer.getGroup().getLevel() >= group.getLevel())) {
            vkBot.printMessage(message.getPeerId(),
                    "❗ Ошибка, для выполнения данной команды необходим статус "
                            + group.getGroupName().toUpperCase() + " и выше!");
            return false;
        }
        return true;
    }

    protected void notEnoughArguments(@NotNull String label) {
        vkBot.printMessage(message.getPeerId(), "❗ Ошибка в синтаксисе, используй: " + label);
    }

    protected void playerOffline(@NotNull String name) {
        vkBot.printMessage(message.getPeerId(), "❗ Ошибка, игрок " + name + "не в сети!");
    }

}
