package me.nekocloud.core.discord.command.feature;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class FindCommand extends DiscordCommand {

    public FindCommand() {
        super("find", "найти");

        setGroup(Group.AXSIDE);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            inputMessage.reply("❗ Ошибка, пиши - /find <игрок>").queue();
            return;
        }

        val targetPlayer = NekoCore.getInstance().getPlayer(args[1]);
        val lang = Language.DEFAULT;
        if (targetPlayer == null) {
            inputMessage.reply(ChatColor.stripColor(lang.getMessage("NOT_FOUND_PLAYER", args[1]))).queue();
            return;
        }

        inputMessage.reply(lang.getMessage("FIND_MESSAGE", targetPlayer.getDisplayName(),
                targetPlayer.getBukkit().getName())).queue();
    }
}
