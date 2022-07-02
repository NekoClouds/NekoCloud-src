package me.nekocloud.nekoapi.donatemenu.commands;

import me.nekocloud.nekoapi.donatemenu.guis.FastMessageGui;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.donatemenu.DonateMenuListener;
import me.nekocloud.nekoapi.donatemenu.FastMessage;
import org.bukkit.entity.Player;

import java.util.Map;

public final class FastMessageCommand implements CommandInterface {

    private final DonateMenuListener donateMenuListener;

    public FastMessageCommand(DonateMenuListener donateMenuListener) {
        this.donateMenuListener = donateMenuListener;

        SpigotCommand command = COMMANDS_API.register("fastmessage", this, "fm");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (args.length == 0) {
            donateMenuListener.open(player, FastMessageGui.class);
            return;
        }

        if (!gamer.isAkio()) {
            gamer.sendMessageLocale("NO_PERMS_GROUP", Group.AKIO.getNameEn());
            return;
        }

        FastMessage fastMessage = null;
        for (Map.Entry<String, FastMessage> entry : FastMessage.getMessages(gamerEntity.getLanguage()).entrySet()) {
            String name = entry.getKey().toLowerCase();
            if (name.startsWith(args[0].toLowerCase())) {
                fastMessage = entry.getValue();
                break;
            }
        }

        if (fastMessage == null) {
            gamerEntity.sendMessageLocale("MESSAGE_NOT_FOUND", args[0]);
            return;
        }

        fastMessage.sendToAll(gamer);
    }
}
