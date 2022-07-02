package me.nekocloud.core.discord.command.account;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.group.GroupManager;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class AccountInfoCommand extends DiscordCommand {

    public AccountInfoCommand() {
        super("инфо", "информация", "аккаунт", "акк", "info");

        setShouldLinkAccount(true);
    }

    @Override
    public void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length < 1) {
            printPlayerInfo(user.getPrimaryAccountName(), inputMessage);
            return;
        }

        val playerGroup = GroupManager.INSTANCE.getPlayerGroup(user.getPrimaryAccountName());

        if (playerGroup.getId() < Group.ADMIN.getId()) {
            inputMessage.reply("❗ У Вас недостаточно прав для получения информации о другом игроке!").queue();
            return;
        }

        printPlayerInfo(args[1], inputMessage);
    }

    private void printPlayerInfo(@NotNull String playerName,
                                 @NotNull Message inputMessage) {

        val playerID = getCore().getNetworkManager().getPlayerID(playerName);
        val vkId = CoreAuth.getAuthManager().loadOrCreate(playerID).getVkId();
        val discord = CoreAuth.getAuthManager().loadOrCreate(playerID).getDiscordTag();

        if (playerID < 0) {
            inputMessage.reply("❗ Игрок " + playerName + " ранее не играл на нашем сервере").queue();
            return;
        }

        val player = NekoCore.getInstance().getOfflinePlayer(playerName);
        val playerInfo = new StringBuilder();

        // Basic information.
        playerInfo.append("Основное:");
        playerInfo.append("\n \uD83D\uDD11 Уникальный ID: ").append(player.getPlayerID());
        playerInfo.append("\n \uD83C\uDFAE Никнейм: ").append(player.getName());
        playerInfo.append("\n \uD83D\uDC8E Статус: ").append(player.getGroup().getName());


        // Twofactor authentication information.
        playerInfo.append("\n\nДвухфакторная авторизация:");
        playerInfo.append("\n \uD83D\uDDDD VK: ");

        if (vkId <= 0)  playerInfo.append("<Не привязано>");
        else            playerInfo.append("@id").append(vkId);

        playerInfo.append("\n \uD83D\uDDDD Дискорд: ");
        if (discord == null) playerInfo.append("<Не привязано>");
        else                 playerInfo.append(discord);


        // FIXME
        // Economy information.
        val offlinePlayer = NekoCore.getInstance().getOfflinePlayer(playerName);
        playerInfo.append("\n\nЭкономика:");
        playerInfo.append("\n Коинов: ").append(NumberUtils.spaced(offlinePlayer.getMoney(PurchaseType.COINS)));
        playerInfo.append("\n Виртов: ").append(NumberUtils.spaced(offlinePlayer.getMoney(PurchaseType.VIRTS)));


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
            playerInfo.append("\n \uD83D\uDCCC Последний вход: ").append(TimeUtil.leftTime(player.getLanguage(), System.currentTimeMillis() - player.getLastOnline())).append(" назад");
        }

        inputMessage.reply(playerInfo).queue();
    }

}
