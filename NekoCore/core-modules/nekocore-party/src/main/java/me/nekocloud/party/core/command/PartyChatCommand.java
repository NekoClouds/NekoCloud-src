package me.nekocloud.party.core.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.party.core.type.PartyManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PartyChatCommand extends CommandExecutor {

    public PartyChatCommand() {
        super("pc", "pchat", "зсрфе");
    }

    @Override
    protected void execute(CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            notEnoughArguments(sender, "PARTY_PREFIX", "PARTY_CHAT_FORMAT");
            return;
        }

        val player = (CorePlayer) sender;
        val party = PartyManager.INSTANCE.getParty(player);
        if (party == null) {
            player.sendMessage("§d§lКОМАНИЯ §8| §fОшибка, ты не состоишь в компании!");
            return;
        }

        String chatMessage = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
        party.alert("§3§lКОМАНИЯ §8➾ §r" + player.getDisplayName() + " §8 " + player.getGroup().getSuffix() + chatMessage);

    }
}
