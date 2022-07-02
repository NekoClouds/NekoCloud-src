//package me.nekocloud.core.discord.command.feature;
//
//import gnu.trove.map.TLongObjectMap;
//import gnu.trove.map.hash.TLongObjectHashMap;
//import lombok.AllArgsConstructor;
//import me.nekocloud.base.gamer.constans.Group;
//import me.nekocloud.core.NekoCore;
//import me.nekocloud.core.api.chat.ChatColor;
//import me.nekocloud.core.api.event.EventHandler;
//import me.nekocloud.core.api.event.EventListener;
//import me.nekocloud.core.common.group.GroupManager;
//import me.nekocloud.core.connection.player.CorePlayer;
//import me.nekocloud.core.discord.command.DiscordCommand;
//import me.nekocloud.core.discord.user.DiscordUser;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.entities.User;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.function.Consumer;
//
//public class ServerChatCommand extends DiscordCommand {
//
//    public ServerChatCommand() {
//        super("chat", "чат", "playerchat");
//
//        setGroup(Group.AXSIDE); // пусть донат покупают, бомжи ебаные
//
//        setShouldLinkAccount(true);
//        setOnlyPrivateMessages(true);
//    }
//
//    @Override
//    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
//        CorePlayer player = NekoCore.getInstance().getPlayer(user.getPrimaryAccountName());
//        BukkitServer bukkitServer;
//
//        if (player == null) {
//            if (args.length == 0) {
//                inputMessage.reply("❗ Ошибка, используй !playerchat <сервер>").queue();
//
//                return;
//            }
//
//            bukkitServer = NekoCore.getInstance().getBukkitServer(args[0]);
//
//            if (bukkitServer == null) {
//                inputMessage.reply("Ошибка, данный сервер не существует или не подключен к Core!").queue();
//
//                return;
//            }
//
//        } else {
//
//            bukkitServer = player.getBukkitServer();
//        }
//
//        openPlayerChat(user, user.getPrimaryAccountName(), channel, bukkitServer,
//                playerMessage -> channel.sendMessage(playerMessage).queue());
//    }
//
//
//    private static final String SESSION_HANDLER_NAME = "server_player_chat";
//    private static final TLongObjectMap<PlayerChatIntegration> CHAT_INTEGRATION_MAP = new TLongObjectHashMap<>();
//
//    static {
//        NekoCore.getInstance().getEventManager().registerListener(new PlayerChatListener());
//    }
//
//    /**
//     * Открыть чат игроков указанного сервера
//     *
//     * @param bukkitServer        - сервер
//     * @param chatMessageConsumer - обработчик пришедшего сообщения от игрока
//     */
//    private void openPlayerChat(@NotNull DiscordUser discordUser,
//                                @NotNull String playerName,
//                                @NotNull MessageChannel channel,
//
//                                @NotNull BukkitServer bukkitServer,
//                                @NotNull Consumer<String> chatMessageConsumer) {
//
//        if (ServerMode.isTyped(bukkitServer, ServerSubModeType.GAME_ARENA)
//                || ServerMode.isTyped(bukkitServer, ServerMode.AUTH)
//                || ServerMode.isTyped(bukkitServer, ServerMode.LIMBO)) {
//
//            channel.sendMessage("❗ Ошибка, данный вид сервера не предназначен для интеграции!").queue();
//            return;
//        }
//
//        PlayerChatIntegration playerChatIntegration = new PlayerChatIntegration(discordUser, channel, discordUser.getPrimaryAccountName(), bukkitServer, chatMessageConsumer);
//        CHAT_INTEGRATION_MAP.put(discordUser.getDiscordID(), playerChatIntegration);
//
//        channel.sendMessage("✅ Интеграция игрового чата от сервера " + bukkitServer.getName() + " была успешно запущена! \n Чтобы отключить напиши /playerchat-close").queue();
//
//        discordUser.createSessionMessageHandler(SESSION_HANDLER_NAME, message -> {
//            //TODO: Добавить проверку на бан и мут игрока
//
//            if (message.contains("/playerchat-close")) {
//                closePlayerChat(discordUser, channel);
//
//                return;
//            }
//
//            // check cooldown
//            String cooldownName = ("chat-integration-").concat(playerName);
//            if (CooldownUtil.hasCooldown(cooldownName)) {
//
//                channel.sendMessage("❗ Ошибка, подожди еще " + NumberUtil.getTime(CooldownUtil.getCooldown(cooldownName))
//                        + " для повторного написания сообщения.").queue();
//                return;
//            }
//
//            if (GroupManager.INSTANCE.getPlayerGroup(playerName).getLevel() >= Group.TRIVAL.getLevel()) {
//                CooldownUtil.putCooldown(cooldownName, 1000 * 30);
//            }
//
//            if (GroupManager.INSTANCE.getPlayerGroup(playerName).getLevel() >= Group.AXSIDE.getLevel()) {
//                CooldownUtil.putCooldown(cooldownName, 1000 * 15);
//            }
//
//            // send message
//            CorePlayer playerSender = NekoCore.getInstance().getOfflinePlayer(playerName);
//
//            for (PlayerChatIntegration anotherChatIntegration : CHAT_INTEGRATION_MAP.valueCollection()) {
//                if (!anotherChatIntegration.bukkitServer.getName().equalsIgnoreCase(playerChatIntegration.bukkitServer.getName())) {
//                    continue;
//                }
//
//                channel.sendMessage("✒ [VK CHAT] " + ChatColor.stripColor(playerSender.getDisplayName()) + ": " + message).queue();
//            }
//
//            for (CorePlayer player : bukkitServer.getOnlinePlayers()) {
//                player.sendMessage(" §9§l[VK CHAT] " + playerSender.getDisplayName() + playerSender.getGroup().getSuffix() + message);
//            }
//        });
//    }
//
//    /**
//     * Закрыть кешированный чат игроков
//     */
//    private void closePlayerChat(@NotNull DiscordUser discordUser,
//                                 @NotNull MessageChannel channel) {
//
//        PlayerChatIntegration playerChatIntegration = CHAT_INTEGRATION_MAP.remove(discordUser.getDiscordID());
//
//        if (playerChatIntegration == null) {
//            return;
//        }
//
//        channel.sendMessage("\uD83D\uDED1 Интеграция игрового чата от сервера " + playerChatIntegration.bukkitServer.getName() + " закрыта!").queue();
//        discordUser.closeSessionMessageHandler(SESSION_HANDLER_NAME);
//    }
//
//
//    @AllArgsConstructor
//    protected class PlayerChatIntegration {
//
//        protected final DiscordUser discordUser;
//        protected final MessageChannel channel;
//        protected final String playerName;
//
//        protected BukkitServer bukkitServer;
//        protected final Consumer<String> chatMessageConsumer;
//
//        public void close() {
//            closePlayerChat(discordUser, channel);
//        }
//    }
//
//    protected static class PlayerChatListener implements EventListener {
//
//        @EventHandler
//        public void onPlayerChat(PlayerChatEvent event) {
//            CorePlayer player = event.getCorePlayer();
//            BukkitServer bukkitServer = event.getBukkitServer();
//
//            String message = ChatColor.stripColor(event.getMessage());
//
//            if (ServerMode.isTyped(bukkitServer, ServerSubModeType.SURVIVAL) && !message.startsWith("!"))
//                return;
//
//            String messageFormat = "✒ [" + bukkitServer.getName() + "] "
//                    + ChatColor.stripColor(player.getDisplayName()) + ": " + (message.startsWith("!") ? message.substring(1) : message);
//
//            for (PlayerChatIntegration playerChatIntegration : CHAT_INTEGRATION_MAP.valueCollection()) {
//                if (!bukkitServer.getName().equalsIgnoreCase(playerChatIntegration.bukkitServer.getName())) {
//                    return;
//                }
//
//                playerChatIntegration.chatMessageConsumer.accept(messageFormat);
//            }
//        }
//
//        @EventHandler
//        public void onPlayerRedirect(PlayerServerRedirectEvent event) {
//            CorePlayer player = event.getCorePlayer();
//            BukkitServer bukkitServer = event.getServerTo();
//
//
//            //FIXME: Интеграция не переносится
//
//            PlayerChatIntegration playerChatIntegration = CHAT_INTEGRATION_MAP.valueCollection()
//                    .stream()
//                    .filter(integration -> integration.playerName.equalsIgnoreCase(player.getName()))
//                    .findFirst()
//                    .orElse(null);
//
//            if (playerChatIntegration != null) {
//                playerChatIntegration.channel.sendMessage("❗ Интеграция игрового чата была перенесена на сервер "
//                        + bukkitServer.getName() + " в связи с переключением твоего аккаунта на указанный!").queue();
//
//                playerChatIntegration.bukkitServer = bukkitServer;
//                CHAT_INTEGRATION_MAP.put(playerChatIntegration.discordUser.getDiscordID(), playerChatIntegration);
//            }
//        }
//
//        @EventHandler
//        public void onPlayerLeave(PlayerLeaveEvent event) {
//            CorePlayer player = event.getCorePlayer();
//
//            CHAT_INTEGRATION_MAP.valueCollection()
//                    .stream()
//                    .filter(integration -> integration.playerName.equalsIgnoreCase(player.getName()))
//                    .findFirst()
//                    .ifPresent(PlayerChatIntegration::close);
//        }
//    }
//
//}
