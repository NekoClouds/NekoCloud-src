package me.nekocloud.packetlib.libraries;

import me.nekocloud.api.ActionBarAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.types.ChatMessageType;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionBarAPIImpl implements ActionBarAPI {

    private final PacketContainer container = NmsAPI.getManager().getPacketContainer();

    @Override
    public void sendBar(Player player, String message) {
        container.sendChatPacket(player, message, ChatMessageType.GAME_INFO);
    }

    @Override
    public void sendBar(Collection<Player> players, String message) {
        for (Player player : players)
            sendBar(player, message);
    }

    @Override
    public void sendAnimatedBar(Player player, String message) {
        String last = (message = message + "                                         ")
                .charAt(0) == '§' ? "§" + message.charAt(1) : "";
        String[] chars = new String[message.length()];
        for (int a = 0; a < message.length(); ++a) {
            String msg;
            if (message.charAt(a) == '§')
                continue;
            if (a < 40) {
                msg = message.substring(0, a + 1);
            } else {
                int c = a - 40;
                msg = message.substring(c, a + 1);
                if (message.charAt(c) == '§') {
                    last = "§" + message.charAt(c + 1);
                    ++a;
                    msg = msg.substring(2);
                }
                msg = last + msg;
            }
            chars[a] = msg;
        }

        BukkitUtil.runTaskAsync(() -> {
            for (String msg : chars) {
                if (msg == null)
                    continue;
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                sendBar(player, msg);
            }
        });
    }
}
