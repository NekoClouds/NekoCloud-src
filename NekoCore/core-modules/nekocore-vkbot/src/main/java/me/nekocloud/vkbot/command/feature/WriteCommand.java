package me.nekocloud.vkbot.command.feature;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class WriteCommand extends VkCommand {

	public WriteCommand() {
		super("write", "напиши");
		setGroup(Group.ADMIN);
		setShouldLinkAccount(true);
	}

	@Override
	protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
		if (args.length < 1){
			notEnoughArguments("!write <текст>");
			return;
		}

		vkBot.printMessage(message.getPeerId(), Joiner.on(" ").join(args));
	}
}
