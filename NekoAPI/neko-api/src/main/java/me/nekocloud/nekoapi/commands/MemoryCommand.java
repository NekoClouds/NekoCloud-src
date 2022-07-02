package me.nekocloud.nekoapi.commands;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public final class MemoryCommand implements CommandInterface {

    private final String spigotName = NekoCloud.getGamerManager().getSpigot().getName();

    public MemoryCommand() {
        val spigotCommand = COMMANDS_API.register("memory", this, "mem");
        spigotCommand.setGroup(Group.ADMIN);
    }

    @Override
    public void execute(
            final @NotNull GamerEntity gamerEntity,
            final String command,
            final String[] args
    ) {
        val runtime = Runtime.getRuntime();
        gamerEntity.sendMessage("Память сервера §d" + spigotName + "§f:");
        gamerEntity.sendMessage(" §c▪ §fАптайм: - §7"
                + TimeUtil.leftTime(Language.RUSSIAN,
                System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()));
        gamerEntity.sendMessage(" §c▪ §fМаксимально - §7" + runtime.maxMemory() / 1048576L + " МБ");
        gamerEntity.sendMessage(" §c▪ §fВыделено - §a" + runtime.totalMemory() / 1048576L + " МБ");
        gamerEntity.sendMessage(" §c▪ §fСвободно - §e" + runtime.freeMemory() / 1048576L + " МБ");
        gamerEntity.sendMessage(" §c▪ §fИспользуется - §c" + (runtime.totalMemory() - runtime.freeMemory())
                / 1048576L + " МБ");
        gamerEntity.sendMessage(" ");
        gamerEntity.sendMessage("TPS: §d" + Arrays.toString(Bukkit.getTPS()));
        gamerEntity.sendMessage(" ");

        if (!NekoCloud.isMisc()) {
            return;
        }

        for (val world : Bukkit.getWorlds()) {
            val tileEntities = new AtomicInteger();

            try {
                for (val chunk : world.getLoadedChunks()) {
                    tileEntities.addAndGet(chunk.getTileEntities().length);
                }

            } catch (java.lang.ClassCastException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Corrupted chunk achievement on world " + world, ex);
            }

            gamerEntity.sendMessage(" §c" + world.getName());
            gamerEntity.sendMessage("  §fЭнтити: §a" + world.getEntities().size());
            gamerEntity.sendMessage("  §fЧанков: §a" + world.getLoadedChunks().length);
            gamerEntity.sendMessage("  §fТайлов: §a" + tileEntities);
        }


    }
}
