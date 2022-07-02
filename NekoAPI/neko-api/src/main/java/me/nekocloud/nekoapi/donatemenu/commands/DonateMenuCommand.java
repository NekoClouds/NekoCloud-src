package me.nekocloud.nekoapi.donatemenu.commands;

import me.nekocloud.nekoapi.donatemenu.guis.MainDonateMenuGui;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.donatemenu.DonateMenuListener;
import org.bukkit.entity.Player;

public final class DonateMenuCommand implements CommandInterface {

    private final DonateMenuListener donateMenuListener;

    public DonateMenuCommand(DonateMenuListener donateMenuListener) {
        this.donateMenuListener = donateMenuListener;

        SpigotCommand command = COMMANDS_API.register("dm", this, "donatemenu");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        donateMenuListener.open(player, MainDonateMenuGui.class);
    }
}
