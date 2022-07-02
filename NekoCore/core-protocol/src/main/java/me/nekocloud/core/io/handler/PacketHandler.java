package me.nekocloud.core.io.handler;

import me.nekocloud.core.io.ChannelWrapper;

public abstract class PacketHandler extends AbstractPacketHandler {

    public void onConnect(ChannelWrapper channel) {
    }

    public void onDisconnect(ChannelWrapper channel) {
    }

    public void onExceptionCaught(ChannelWrapper channel, Throwable cause) {
    }
}
