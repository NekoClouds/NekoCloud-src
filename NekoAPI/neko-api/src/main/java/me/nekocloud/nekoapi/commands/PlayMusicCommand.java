package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.Sound;

public class PlayMusicCommand implements CommandInterface {

    public PlayMusicCommand() {
        val command = COMMANDS_API.register("playmusic", this);
        command.setGroup(Group.ADMIN);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        val gamer = (BukkitGamer) gamerEntity;
        if (args.length < 3) {
            gamer.sendMessage("Не хватает аргументов! <music> <volume> <pitch>");
            return;
        }

        try {
            Sound sound = Sound.valueOf(args[0]);
            gamer.playSound(sound, Float.parseFloat(args[1]), Float.parseFloat(args[2]));
        } catch (Exception e) {
            gamer.sendMessage("Что-то обосралось!");
        }

    }
}
