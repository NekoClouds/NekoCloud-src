package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class CloudCrashCommand implements CommandInterface {

	public CloudCrashCommand() {
		val command = COMMANDS_API.register("cloudcrash", this);
		command.setGroup(Group.OWNER);
	}

	@Override
	public void execute(
			final GamerEntity gamerEntity,
			final String command,
			final String @NotNull[] args
	) {
		if (args.length < 1) {
			COMMANDS_API.notEnoughArguments(gamerEntity, "/cloudcrash <ник>");
			return;
		}

		val targetGamer = GAMER_MANAGER.getGamer(args[0]);
		if (targetGamer == null) {
			COMMANDS_API.playerOffline(gamerEntity, args[0]);
			return;
		}

		val targetPlayer = targetGamer.getPlayer();

		targetPlayer.spawnParticle(Particle.CLOUD, 2.14748365E9F,
				2.14748365E9F, 2.14748365E9F, 100_000,
				2.14748365E9F, 2.14748365E9F, 2.14748365E9F,2147483647);

        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
        targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_CREEPER_DEATH, 1.0F, 1.0F);

        gamerEntity.sendMessage(targetPlayer.getDisplayName() + "§f успешно получил пиздюлей");

	}
}
