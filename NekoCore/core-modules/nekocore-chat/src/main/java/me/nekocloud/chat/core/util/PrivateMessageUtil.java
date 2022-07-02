package me.nekocloud.chat.core.util;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
public class PrivateMessageUtil {

    public void sendMessage(@NotNull CorePlayer senderPlayer,
                            @NotNull CorePlayer targetPlayer,

                            @NotNull String message
    ) {
        if (senderPlayer.equals(targetPlayer)) {
            senderPlayer.sendMessageLocale("POST_MESSAGE_SELF_ME");
            return;
        }

        if (!targetPlayer.isOnline()) {
            senderPlayer.sendMessageLocale("NO_FOUND_PLAYER", targetPlayer);
            return;
        }

        if (!targetPlayer.getSetting(SettingsType.PRIVATE_MESSAGE)) {
            senderPlayer.sendMessageLocale("POST_MESSAGE_TARGET_IGNORE_ALL");
            return;
        }

        IntList ignoreList = targetPlayer.getData("ignore_list");
        if (ignoreList.contains(senderPlayer.getPlayerID())) {
            senderPlayer.sendMessageLocale("PLAYER_IS_IGNORED_YOU", targetPlayer.getName());
            return;
        }

        targetPlayer.sendMessageLocale("RECEIVED_MESSAGE_FORMAT",
                senderPlayer.getDisplayName(), "Я", message);
        senderPlayer.sendMessageLocale("RECEIVED_MESSAGE_FORMAT",
                "Я", targetPlayer.getDisplayName(), message);

        targetPlayer.addData("reply", senderPlayer.getPlayerID());

        for (val staffPlayer : NekoCore.getInstance().getOnlinePlayers(corePlayer -> corePlayer.isStaff()
                        && !(corePlayer == targetPlayer)
                        && !(corePlayer == senderPlayer))
        ) {
            staffPlayer.sendMessage("§d§lСЛЕЖКА §8| §r" + staffPlayer.getLanguage().getMessage("POST_MESSAGE_CHAT",
                            senderPlayer.getDisplayName(), targetPlayer.getDisplayName(), message));
        }
    }

    public void replyMessage(@NotNull CorePlayer senderPlayer,
                             @NotNull String message
    ) {

        val targetPlayer = NekoCore.getInstance().getPlayer(Integer.parseInt(senderPlayer.getData("reply")));
        if (targetPlayer == null) {
            senderPlayer.sendMessageLocale("POST_MESSAGE_NO_REPLY");
            return;
        }

        if (!targetPlayer.getSetting(SettingsType.PRIVATE_MESSAGE)) {
            senderPlayer.sendMessageLocale("POST_MESSAGE_TARGET_IGNORE_ALL");
            return;
        }

        IntList ignoreList = targetPlayer.getData("ignore_list");
        if (ignoreList.contains(senderPlayer.getPlayerID())) {
            senderPlayer.sendMessageLocale("PLAYER_IS_IGNORED_YOU", targetPlayer.getDisplayName());
            return;
        }

        sendMessage(senderPlayer, targetPlayer, message);
        targetPlayer.addData("reply", senderPlayer.getPlayerID());
    }
}
