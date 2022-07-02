package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.effect.ParticleAPI;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import org.jetbrains.annotations.NotNull;

public final class FwCommand implements CommandInterface {

    private final ParticleAPI particleAPI = NekoCloud.getParticleAPI();

    public FwCommand() {
        val spigotCommand = COMMANDS_API.register("fireworks", this, "fw");
        spigotCommand.setGroup(Group.HEGENT);
        spigotCommand.setOnlyGame(true);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(final @NotNull GamerEntity gamerEntity, final String command, final String[] args) {
        final BukkitGamer gamer = (BukkitGamer) gamerEntity;

        particleAPI.shootRandomFirework(gamer.getPlayer());
        gamerEntity.sendMessageLocale("FW");
    }
}