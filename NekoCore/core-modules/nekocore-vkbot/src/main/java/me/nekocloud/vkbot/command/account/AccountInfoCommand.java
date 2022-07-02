package me.nekocloud.vkbot.command.account;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.network.PlaytimeManager;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class AccountInfoCommand extends VkCommand {

    public AccountInfoCommand() {
        super("инфо", "информация", "аккаунт", "акк", "info");

        setShouldLinkAccount(true);
    }

    @Override // TODO: переписать этот ебаный говнокод
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (!message.getForwardedMessages().isEmpty()) {
            if (!hasPerms(vkUser, Group.ADMIN)) return;
            val forwardedMessage = message.getForwardedMessages().get(0);
            val targetVkUser = VkUser.getVkUser(forwardedMessage.getUserId());
            if (!targetVkUser.hasPrimaryAccount()) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, пользователь [id" + targetVkUser.getVkId() + "|id" + targetVkUser.getVkId() + "] не привязал игровой аккаунт к своему VK!");
                return;
            }

            printPlayerInfo(targetVkUser.getPrimaryAccountName(), message, vkBot);
            return;
        }

        if (args.length == 0) {
            printPlayerInfo(vkUser.getPrimaryAccountName(), message, vkBot);
            return;
        }

        if (!hasPerms(vkUser, Group.ADMIN)) return;
        printPlayerInfo(args[0], message, vkBot);
    }

    private void printPlayerInfo(@NotNull String playerName,
                                 @NotNull Message message,
                                 @NotNull VkBot vkBot) {

        val playerID = NekoCore.getInstance().getNetworkManager().getPlayerID(playerName);
        if (playerID < 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Игрок " + playerName + " ранее не играл на нашем проекте");
            return;
        }

        val vkId = CoreAuth.getAuthManager().loadOrCreate(playerID).getVkId();
        val discord = CoreAuth.getAuthManager().loadOrCreate(playerID).getDiscordTag();

        val player = NekoCore.getInstance().getOfflinePlayer(playerName);

        StringBuilder playerInfo = new StringBuilder();

        // Basic information.
        playerInfo.append("Основное:");
        playerInfo.append("\n \uD83D\uDD11 Уникальный ID: ").append(player.getPlayerID());
        playerInfo.append("\n \uD83C\uDFAE Никнейм: ").append(player.getName());
        playerInfo.append("\n \uD83D\uDC8E Статус: ").append(ChatColor.stripColor(player.getGroup().getName()));


        // Twofactor authentication information.
        playerInfo.append("\n\nДвухфакторная авторизация:");
        playerInfo.append("\n \uD83D\uDDDD VK: ");

        if (vkId <= 0)  playerInfo.append("<Не привязано>");
        else            playerInfo.append("@id").append(vkId);

        playerInfo.append("\n \uD83D\uDDDD Дискорд: ");
        playerInfo.append(Objects.requireNonNullElse(discord, "<Не привязано>"));

        // FIXME
        // Economy information.
        val offlinePlayer = NekoCore.getInstance().getOfflinePlayer(playerName);
        playerInfo.append("\n\nЭкономика:");
        playerInfo.append("\n Коинов: ").append(NumberUtils.spaced(offlinePlayer.getMoney(PurchaseType.COINS)));
        playerInfo.append("\n Виртов: ").append(NumberUtils.spaced(offlinePlayer.getMoney(PurchaseType.VIRTS)));

        // Playtime information.
        val playtime = PlaytimeManager.INSTANCE.getPlayTime(playerID);
        playerInfo.append("\n\nНаиграно: ").append(TimeUtil.leftTime(Language.RUSSIAN, playtime));

        // Level information.
        int level       = player.getLevelNetwork();
        int exp         = player.getExp();
        int maxExp      = player.getExpNextLevel();
        int percent     = StringUtil.onPercent(maxExp, exp);

        playerInfo.append("\n\nИгровой уровень:");
        playerInfo.append("\n \uD83D\uDD38 Уровень: ").append(NumberUtils.spaced(level)).append(" LVL");
        playerInfo.append("\n \uD83D\uDD38 Опыт: ").append(NumberUtils.spaced(maxExp)).append(" EXP из ").append(NumberUtils.spaced(exp)).append(" EXP");

        playerInfo.append("\n\n  Собрано ").append(percent).append("% опыта до следующего уровня,");
        playerInfo.append("\n  До ").append(level + 1).append(" уровня необходимо еще ").append(NumberUtils.spaced(exp - maxExp)).append(" EXP (").append(100 - percent).append("%)");


        // Status information.
        playerInfo.append("\n\nДополнительно:");

        if (NekoCore.getInstance().getPlayer(playerID) != null) {
            playerInfo.append("\n Online (в сети)");
            playerInfo.append("\n \uD83E\uDDC0 Сервер: ").append(player.getBukkit() == null ? "<Не инициализировано>" : player.getBukkit().getName().toLowerCase());

        } else {
            playerInfo.append("\n Offline (не в сети)");
            playerInfo.append("\n \uD83D\uDCAC Последний сервер: ").append(player.getOfflineData().getLastServerName().toLowerCase());
            playerInfo.append("\n \uD83D\uDCCC Последний вход: ").append(TimeUtil.leftTime(Language.RUSSIAN, System.currentTimeMillis() - player.getLastOnline())).append(" назад");
        }

        new Message()
                .peerId(message.getPeerId())
                .forwardedMessages(message)

                .body(playerInfo.toString())
                .send(vkBot);
    }

}
