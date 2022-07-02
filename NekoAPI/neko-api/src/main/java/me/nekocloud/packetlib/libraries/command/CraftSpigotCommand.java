package me.nekocloud.packetlib.libraries.command;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.base.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CraftSpigotCommand extends Command implements SpigotCommand {
    static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    static final int FINAL_COOLDOWN = 5;

    final CommandManager commandManager;

    @Setter
    CommandInterface commandInterface;
    @Setter
    CommandTabComplete commandTabComplete;
    @Getter @Setter
    Group group;
    @Setter
    boolean onlyPlayers;
    @Setter
    boolean onlyGame;
    @Setter
    boolean onlyConsole;

    int cooldown;
    String cooldownType;

    CraftSpigotCommand(CommandManager commandManager, String command,
                       CommandInterface commandInterface, String... aliases) {
        super(command, "", "", Arrays.asList(aliases));
        this.commandManager = commandManager;

        this.commandInterface = commandInterface;

        this.group = Group.PIDOR;

        this.cooldown = FINAL_COOLDOWN;
        this.cooldownType = "command_cooldown";

        registerCommand();

        commandManager.register(this);
    }

    @SuppressWarnings("unchecked")
    private void registerCommand() {
        List<String> commands = new ArrayList<>(getAliases());
        commands.add(this.getName());
        try {
            Method register = SimpleCommandMap.class.getDeclaredMethod("register",
                    String.class, Command.class, Boolean.TYPE, String.class);
            register.setAccessible(true);
            for (String cmd : commands) {
                register.invoke(commandManager.getCommandMap(), cmd, this, !(this.getName().equals(cmd)), "nekocloud");
            }

            register.setAccessible(false);

            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            Map<String, Command> map = (Map) knownCommands.get(commandManager.getCommandMap());
            for (String cmd : commands) {
                map.put(cmd.toLowerCase(), this);
            }

            knownCommands.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCooldown(int second, String type) {
        this.cooldown = second * 20;
        this.cooldownType = type;
    }

    @Override
    public int getSecondCooldown() {
        return cooldown / 20;
    }

    @Override
    public int getLevel() {
        return group.getLevel();
    }

    @Override
    public void disable() {
        NekoCloud.getCommandsAPI().disableCommand((SpigotCommand) this);
    }

    @Override
    public Command getCommand() {
        return this;
    }

    @Override
    public void setMinimalGroup(int level) {
        setMinimalGroup(Group.getGroupByLevel(level));
    }

    @Override
    public void setMinimalGroup(Group group) {
        this.group = group;
    }

    @Override
    public final boolean execute(CommandSender commandSender, String command, String[] args) {
        GamerEntity gamerEntity = GAMER_MANAGER.getEntity(commandSender);
        if (gamerEntity == null) {
            return false;
        }

        boolean checkPlayer = commandSender instanceof Player;

        if (!checkPlayer && onlyPlayers) {
            gamerEntity.sendMessage("§cДанную команду нельзя использовать из консоли!");
            return false;
        }

        if(checkPlayer && onlyConsole) {
            gamerEntity.sendMessageLocale("COMMAND_ONLY_CONSOLE");
            return false;
        }

        if (checkPlayer) {
            final Player player = (Player) commandSender;
            final BukkitGamer gamer = (BukkitGamer) gamerEntity;
            if (!player.isOnline()) {
                return false;
            }

            if (gamer.getGroup().getLevel() < Group.ADMIN.getLevel()) {
                if (Cooldown.hasCooldown(player.getName(), cooldownType)) {
                    if (cooldown != FINAL_COOLDOWN) {
                        Language lang = gamerEntity.getLanguage();
                        int time = Cooldown.getSecondCooldown(player.getName(), cooldownType);
                        gamerEntity.sendMessage(String.format(lang.getMessage("COOLDOWN"),
                                String.valueOf(time), CommonWords.SECONDS_1.convert(time, lang)));
                    }
                    return false;
                }
                Cooldown.addCooldown(player.getName(), cooldownType, cooldown);
            }

            if (onlyGame && NekoCloud.isGame()) {
                gamerEntity.sendMessageLocale("ERROR_COMMAND_IN_GAME");
                return false;
            }

            if (gamer.getGroup().getLevel() < group.getLevel()) {
                if (group.getLevel() >= Group.ADMIN.getLevel() || group == Group.MODERATOR || group == Group.SRMODERATOR) {
                    gamer.sendMessageLocale("NO_PERMS");
                } else {
                    gamer.sendMessageLocale("NO_PERMS_GROUP", group.getNameEn());
                }
                return false;
            }
        }

        this.fixArgs(args);
        this.commandInterface.execute(gamerEntity, command, args);
        return true;
    }

    private void fixArgs(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.length() > 2) {
                    char c0 = arg.charAt(0);
                    char cl = arg.charAt(arg.length() - 1);
                    if ((c0 == '[' && cl == ']') || (c0 == '<' && cl == '>')) {
                        args[i] = arg.substring(1, arg.length() - 1);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean testPermissionSilent(CommandSender commandSender) {
        if (onlyPlayers && !(commandSender instanceof Player)) {
            return false;
        }

        final GamerEntity gamerEntity = GAMER_MANAGER.getEntity(commandSender);
        if (gamerEntity == null) {
            return false;
        }

        if (gamerEntity instanceof BukkitGamer gamer) {
            return gamer.getGroup().getLevel() >= group.getLevel();
        }

        return true;
    }

    @Override
    public final List<String> tabComplete(CommandSender commandSender, String alias, String[] args) throws IllegalArgumentException {
        if (onlyPlayers && !(commandSender instanceof Player)) {
            return ImmutableList.of();
        }

        GamerEntity gamerEntity = GAMER_MANAGER.getEntity(commandSender);
        if (gamerEntity == null) {
            return ImmutableList.of();
        }

        if (gamerEntity instanceof BukkitGamer gamer) {
            if (gamer.getGroup().getLevel() < group.getLevel()) {
                return ImmutableList.of();
            }
        }

        if (commandTabComplete != null) {
            List<String> complete = commandTabComplete.getComplete(gamerEntity, alias, args);
            if (complete == null) {
                return super.tabComplete(commandSender, alias, args);
            }
            return complete.stream()
                    .limit(15)
                    .collect(Collectors.toList());
        }

        return super.tabComplete(commandSender, alias, args);
    }
}
