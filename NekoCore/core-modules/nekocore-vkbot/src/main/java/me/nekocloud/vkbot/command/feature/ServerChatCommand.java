package me.nekocloud.vkbot.command.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerQuitEvent;
import me.nekocloud.core.api.event.player.PlayerRedirectEvent;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ServerChatCommand extends VkCommand {

    public ServerChatCommand() {
        super("chat", "чат", "playerchat");

        setGroup(Group.AXSIDE); // пусть донат покупают, бомжи ебаные

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        CorePlayer player = NekoCore.getInstance().getPlayer(vkUser.getPrimaryAccountName());
        Bukkit bukkitServer;

        if (player == null) {
            if (args.length == 0) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй !playerchat <сервер>");

                return;
            }

            bukkitServer = NekoCore.getInstance().getBukkit(args[0]);

            if (bukkitServer == null) {
                vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный сервер не существует или не подключен к Core!");

                return;
            }

        } else {

            bukkitServer = player.getBukkit();
        }

        openPlayerChat(message.getPeerId(), vkUser, vkUser.getPrimaryAccountName(), vkBot, bukkitServer,
                playerMessage -> vkBot.printMessage(message.getPeerId(), playerMessage));
    }


    private static final String SESSION_HANDLER_NAME = "server_player_chat";
    private static final Int2ObjectMap<PlayerChatIntegration> CHAT_INTEGRATION_MAP = new Int2ObjectOpenHashMap<>();

    static {
        NekoCore.getInstance().getEventManager().register(new PlayerChatListener());
    }

    /**
     * Открыть чат игроков указанного сервера
     *
     * @param bukkitServer        - сервер
     * @param chatMessageConsumer - обработчик пришедшего сообщения от игрока
     */
    private void openPlayerChat(int peerId,

                                @NotNull VkUser vkUser,
                                @NotNull String playerName,
                                @NotNull VkBot vkBot,

                                @NotNull Bukkit bukkitServer,
                                @NotNull Consumer<String> chatMessageConsumer) {

//        if (ServerMode.isTyped(bukkitServer, ServerSubModeType.GAME_ARENA)
//                || ServerMode.isTyped(bukkitServer, ServerMode.AUTH)
//                || ServerMode.isTyped(bukkitServer, ServerMode.LIMBO)) {
//
//            vkBot.printMessage(peerId, "❗ Ошибка, данный вид сервера не предназначен для интеграции!");
//            return;
//        }

        PlayerChatIntegration playerChatIntegration = new PlayerChatIntegration(vkUser, vkUser.getPrimaryAccountName(), peerId, bukkitServer, chatMessageConsumer);
        CHAT_INTEGRATION_MAP.put(peerId, playerChatIntegration);

        new Message()
                .peerId(peerId)
                .body("✅ Интеграция игрового чата от сервера " + bukkitServer.getName() + " была успешно запущена!")

                .keyboard(false, true)
                .button(KeyboardButtonColor.NEGATIVE, 0, new TextButtonAction("/playerchat-close", "Закрыть чат"))
                .message()

                .send(vkBot);

        vkUser.createSessionMessageHandler(SESSION_HANDLER_NAME, message -> {
            //TODO: Добавить проверку на бан и мут игрока

            if (message.contains("/playerchat-close")) {
                closePlayerChat(peerId, vkUser, vkBot);

                return;
            }

            // check cooldown
            String cooldownName = ("chat-integration-").concat(playerName);
//            if (CooldownUtil.hasCooldown(cooldownName)) {
//
//                vkBot.printAndDeleteMessage(peerId, "❗ Ошибка, подожди еще " + NumberUtil.getTime(CooldownUtil.getCooldown(cooldownName))
//                        + " для повторного написания сообщения.");
//                return;
//            }

//            if (GroupManager.INSTANCE.getPlayerGroup(playerName).getLevel() >= Group.TRIVAL.getLevel()) {
//                CooldownUtil.putCooldown(cooldownName, 1000 * 30);
//            }
//
//            if (GroupManager.INSTANCE.getPlayerGroup(playerName).getLevel() >= Group.AXSIDE.getLevel()) {
//                CooldownUtil.putCooldown(cooldownName, 1000 * 15);
//            }

            // send message
            CorePlayer playerSender = NekoCore.getInstance().getOfflinePlayer(playerName);

            for (PlayerChatIntegration anotherChatIntegration : CHAT_INTEGRATION_MAP.values()) {
                if (!anotherChatIntegration.bukkitServer.getName().equalsIgnoreCase(playerChatIntegration.bukkitServer.getName())) {
                    continue;
                }

                vkBot.printMessage(anotherChatIntegration.peerId, "✒ [VK CHAT] " + ChatColor.stripColor(playerSender.getDisplayName()) + ": " + message);
            }

            for (CorePlayer player : bukkitServer.getOnlinePlayers()) {
                player.sendMessage(" §9§l[VK CHAT] " + playerSender.getDisplayName() + playerSender.getGroup().getSuffix() + message);
            }
        });
    }

    /**
     * Закрыть кешированный чат игроков
     */
    private void closePlayerChat(int peerId,
                                 @NotNull VkUser vkUser,
                                 @NotNull VkBot vkBot) {

        PlayerChatIntegration playerChatIntegration = CHAT_INTEGRATION_MAP.remove(peerId);

        if (playerChatIntegration == null) {
            return;
        }

        vkBot.printMessage(peerId, "\uD83D\uDED1 Интеграция игрового чата от сервера " + playerChatIntegration.bukkitServer.getName() + " закрыта!");
        vkUser.closeSessionMessageHandler(SESSION_HANDLER_NAME);
    }


    @AllArgsConstructor
    protected class PlayerChatIntegration {

        protected final VkUser vkUser;
        protected final String playerName;

        protected final int peerId;

        protected Bukkit bukkitServer;
        protected final Consumer<String> chatMessageConsumer;

        public void close() {
            closePlayerChat(peerId, vkUser, VkBot.INSTANCE);
        }
    }

    protected static class PlayerChatListener implements EventListener {

//        @EventHandler
//        public void onPlayerChat(PlayerChat event) {
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

        @EventHandler
        public void onPlayerRedirect(PlayerRedirectEvent event) {
            CorePlayer player = event.getCorePlayer();
            Bukkit bukkitServer = event.getTo();


            //FIXME: Интеграция не переносится

            PlayerChatIntegration playerChatIntegration = CHAT_INTEGRATION_MAP.values()
                    .stream()
                    .filter(integration -> integration.playerName.equalsIgnoreCase(player.getName()))
                    .findFirst()
                    .orElse(null);

            if (playerChatIntegration != null) {
                VkBot.INSTANCE.printMessage(playerChatIntegration.peerId, "❗ Интеграция игрового чата была перенесена на сервер "
                        + bukkitServer.getName() + " в связи с переключением твоего аккаунта на указанный!");

                playerChatIntegration.bukkitServer = bukkitServer;
                CHAT_INTEGRATION_MAP.put(playerChatIntegration.peerId, playerChatIntegration);
            }
        }

        @EventHandler
        public void onPlayerLeave(PlayerQuitEvent event) {
            CorePlayer player = event.getCorePlayer();

            CHAT_INTEGRATION_MAP.values()
                    .stream()
                    .filter(integration -> integration.playerName.equalsIgnoreCase(player.getName()))
                    .findFirst()
                    .ifPresent(PlayerChatIntegration::close);
        }
    }

}
