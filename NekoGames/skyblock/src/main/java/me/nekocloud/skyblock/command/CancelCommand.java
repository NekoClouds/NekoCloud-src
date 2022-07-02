package me.nekocloud.skyblock.command;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.skyblock.craftisland.CraftSkyGamer;
import me.nekocloud.skyblock.api.entity.SkyGamer;
import org.bukkit.entity.Player;

public final class CancelCommand extends RequestCommand {

    public CancelCommand() {
        super("cancel", "отменить", "против");
    }

    @Override
    void accept(Player sender, Player who) {
        //who приглашает
        //sender принимает

        SkyGamer skyGamer = MANAGER.getSkyGamer(sender);
        if (skyGamer == null)
            return;

        ((CraftSkyGamer)skyGamer).getRequests().remove(who.getName());
        BukkitGamer gamerSender = GAMER_MANAGER.getGamer(sender);
        BukkitGamer gamerWho = GAMER_MANAGER.getGamer(who);
        if (gamerSender != null) {
            sendMessage(gamerSender, "ISLAND_CANCEL", who.getDisplayName());
        }

        if (gamerWho != null) {
            sendMessage(gamerWho, "ISLAND_CANCEL_YOU", sender.getDisplayName());
        }
    }
}
