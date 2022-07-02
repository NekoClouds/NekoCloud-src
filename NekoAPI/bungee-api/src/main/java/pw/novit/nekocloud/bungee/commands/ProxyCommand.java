package pw.novit.nekocloud.bungee.commands;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.util.Cooldown;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.gamer.BungeeEntityManager;

import static lombok.AccessLevel.PRIVATE;

/**
 * #КодНовита
 */
@Setter
@FieldDefaults(level = PRIVATE)
public abstract class ProxyCommand<T extends Plugin> extends Command implements TabExecutor {

    protected T plugin;

    final int FINAL_COOLDOWN = 3;

    Group group = Group.PIDOR;
    boolean onlyPlayers;

    int cooldown;
    String cooldownType;

    public ProxyCommand(final T plugin, final String name, final String... aliases) {
        super(name,null, aliases);
        register(plugin);

        this.cooldown = FINAL_COOLDOWN;
        this.cooldownType = "command_cooldown";
    }

    private void register(final T plugin) {
        this.plugin = plugin;
        BungeeCord.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] strings) {
        val entity = BungeeEntityManager.getEntity(commandSender);
        if (entity == null)
            return;

        val checkPlayer = commandSender instanceof ProxiedPlayer;

        if (!checkPlayer && onlyPlayers) {
            entity.sendMessage("§cДанную команду нельзя использовать из консоли!");
            return;
        }

        if (checkPlayer) {
            val gamer = BungeeGamer.getGamer(commandSender.getName());
            if (gamer == null)
                return;

            val player = (ProxiedPlayer) commandSender;
            if (!player.isConnected())
                return;

            if (gamer.getGroup().getLevel() < Group.ADMIN.getLevel()) {
                if (Cooldown.hasCooldown(player.getName(), cooldownType)) {
                    if (cooldown != FINAL_COOLDOWN) {
                        val lang = entity.getLanguage();
                        val time = Cooldown.getSecondCooldown(player.getName(), cooldownType);
                        entity.sendMessage(String.format(lang.getMessage("COOLDOWN"),
                                time, CommonWords.SECONDS_1.convert(time, lang)));
                    }
                    return;
                }
                Cooldown.addCooldown(player.getName(), cooldownType, cooldown);
            }

            val minLevel = group.getLevel();
            if (gamer.getGroup().getLevel() < minLevel) {
                if (minLevel >= Group.ADMIN.getLevel() || group == Group.MODERATOR || group == Group.SRMODERATOR) {
                    entity.sendMessageLocale("NO_PERMS");
                } else {
                    entity.sendMessageLocale("NO_PERMS_GROUP", group.getNameEn());
                }
                return;
            }
        }

        this.execute(entity, strings);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (onlyPlayers && !(sender instanceof ProxiedPlayer))
            return ImmutableSet.of();

        val entity = BungeeEntityManager.getEntity(sender);
        if (entity == null) return ImmutableSet.of();

        if (entity instanceof BungeeGamer gamer) {
            if (gamer.getGroup().getLevel() < group.getLevel()) {
                return ImmutableSet.of();
            }
        }

        Iterable<String> complete = tabComplete(entity, args);
        if (complete == null) return ImmutableSet.of();

        return complete;
    }

    public abstract void execute(final BungeeEntity entity, final String[] args);
    public abstract Iterable<String> tabComplete(final BungeeEntity entity, final String[] args);

    public void setCooldown(int second, String type) {
        this.cooldown = second * 20;
        this.cooldownType = type;
    }

    public int getSecondCooldown() {
        return cooldown / 20;
    }

    public void notEnoughArguments(final @NotNull BungeeEntity entity, final String prefix, @NotNull final String key) {
        val lang = entity.getLanguage();
        entity.sendMessageLocale("ERROR_COMMAND", lang.getMessage(prefix), lang.getMessage(key));
    }

    public void playerOffline(final @NotNull BungeeEntity entity, final @NotNull String name) {
        entity.sendMessageLocale("NO_FOUND_PLAYER", name);
    }

    public void playerNeverPlayed(final @NotNull BungeeEntity entity, final @NotNull String name) {
        entity.sendMessageLocale("NO_NEVER_PLAYER", name);
    }

    public void showHelp(final @NotNull BungeeEntity entity,
                         final @NotNull String command,
                         final @NotNull String key) {
        entity.sendMessageLocale("HELP_PAPER_FOR", command);
        entity.sendMessagesLocale(key);
    }
}
