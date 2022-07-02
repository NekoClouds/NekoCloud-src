package me.nekocloud.packetlib.libraries;

import me.nekocloud.api.TitleAPI;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.types.TitleActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleAPIImpl implements TitleAPI {

    private final PacketContainer container = NmsAPI.getManager().getPacketContainer();

    @Override
    public void sendTitle(Player player, String msgTitle, String msgSubTitle,
                          long fadeInTime, long stayTime, long fadeOutTime) {

        container.sendTitlePacket(player, TitleActionType.TITLE, msgTitle);

        if (!msgSubTitle.equals(""))
            container.sendTitlePacket(player, TitleActionType.SUBTITLE, msgSubTitle);

        container.sendTitlePacket(player, (int)fadeInTime, (int)stayTime, (int)fadeOutTime);
    }

    @Override
    public void sendTitle(Player player, String title) {
        sendTitle(player, title, "");
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle) {
        sendTitle(player, title, subTitle, 60, 60, 60);
    }

    @Override
    public void sendTitleAll(String title) {
        Bukkit.getOnlinePlayers().forEach(pl -> sendTitle(pl, title));
    }

    @Override
    public void sendTitleAll(String title, String subTitle) {
        Bukkit.getOnlinePlayers().forEach(pl -> sendTitle(pl, title, subTitle));
    }

    @Override
    public void sendTitleAll(String title, String subtitle, long fadeInTime, long stayTime, long fadeOutTime) {
        Bukkit.getOnlinePlayers().forEach(pl -> sendTitle(pl, title, subtitle, fadeInTime, stayTime, fadeOutTime));
    }
}
