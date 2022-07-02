package me.nekocloud.core.api.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public abstract class CommandExecutor {

    int FINAL_COOLDOWN = 5;

    NekoCore core;
    String command;
    String[] aliases;

    @NonFinal int cooldown;
    @NonFinal String cooldownType;


    public CommandExecutor(
            final @NotNull String command,
            final @NotNull String... aliases
    ) {
        this.core = NekoCore.getInstance();
        this.command = command;
        this.aliases = aliases;

        this.cooldown = FINAL_COOLDOWN;
        this.cooldownType = "command_cooldown";
    }

    /**
     * Группа с которой можно использовать команду
     */
    @NonFinal @Setter Group group = Group.DEFAULT;


    /**
     * Команда доступна только игрокам
     */
    @NonFinal @Setter boolean onlyPlayers = false;

    /**
     * Команда доступна только авторизированным игрока
     */
    @NonFinal @Setter boolean onlyAuthorized = true;

    /**
     * Команду можно использовать на логин сервере
     */
    @NonFinal @Setter boolean loginServer = false;

    protected void setCooldown(int second, String type) {
        this.cooldown = second * 20;
        this.cooldownType = type;
    }

    /**
     * Выполнение команды
     * @param sender - отправитель
     * @param args - аргументы
     */
    protected abstract void execute(final CommandSender sender, final String @NotNull[] args);

    public void onExecute(
            final @NotNull CommandSender sender,
            final @NotNull String[] strings
    ) {
        boolean checkPlayer = sender instanceof CorePlayer;

        if (!checkPlayer && onlyPlayers) {
            sender.sendMessage("§cДанную команду нельзя использовать из консоли!");
            return;
        }

        if (checkPlayer) {
            val player = (CorePlayer) sender;
            if (!player.isOnline())
                return;

            if (player.getGroup().getLevel() < Group.ADMIN.getLevel()) {
                if (Cooldown.hasCooldown(player.getName(), cooldownType)) {
                    if (cooldown != FINAL_COOLDOWN) {
                        val lang = player.getLanguage();
                        int time = Cooldown.getSecondCooldown(player.getName(), cooldownType);
                        sender.sendMessage(String.format(lang.getMessage("COOLDOWN"),
                                time, CommonWords.SECONDS_1.convert(time, lang)));
                    }
                    return;
                }
                Cooldown.addCooldown(player.getName(), cooldownType, cooldown);
            }

            val minLevel = group.getLevel();
            if (player.getGroup().getLevel() < minLevel) {
                if (minLevel >= Group.ADMIN.getLevel() || group == Group.MODERATOR || group == Group.SRMODERATOR) {
                    sender.sendMessageLocale("NO_PERMS");
                } else {
                    sender.sendMessageLocale("NO_PERMS_GROUP", group.getNameEn());
                }
                return;
            }

//            if (onlyAuthorized && (!CoreAuthManager.INSTANCE.hasAuthSession(player) || CoreAuthManager.INSTANCE.hasTwofactorSession(player.getName()))) {
//                sender.sendMessageLocale("ERROR_ONLY_AUTH_PLAYER");
//                return;
//            }
//
//            // Все команды по дефолту нельзя юз в логин сервере.
//            if (!loginServer && ServerMode.isTyped(player.getBukkitServer(), ServerMode.AUTH)) {
//                sender.sendMessageLocale("ERROR_LOGIN_SERVER");
//                return;
//            }
        }

        this.execute(sender, strings);
    }

    public void notEnoughArguments(
            final @NotNull CommandSender sender,
            final String prefix,
            final String key
    ) {
        val lang = sender.getLanguage();
        sender.sendMessageLocale("ERROR_COMMAND", lang.getMessage(prefix), lang.getMessage(key));
    }

    public void playerOffline(final @NotNull CommandSender sender, final String name) {
        sender.sendMessageLocale("NO_FOUND_PLAYER", name);
    }

    public void playerNeverPlayed(final @NotNull CommandSender sender, final String name) {
        sender.sendMessageLocale("NO_NEVER_PLAYER", name);
    }

    public boolean hasIdentifier(final @NotNull CommandSender sender, final String name) {
        if (!getCore().getNetworkManager().hasIdentifier(name)) {
            playerNeverPlayed(sender, name);
            return false;
        }
        return true;
    }
}
