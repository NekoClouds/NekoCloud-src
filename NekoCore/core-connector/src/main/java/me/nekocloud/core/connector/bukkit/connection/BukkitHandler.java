package me.nekocloud.core.connector.bukkit.connection;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.OnlineGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.sections.MoneySection;
import me.nekocloud.base.gamer.sections.NetworkingSection;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.connector.bukkit.BukkitConnector;
import me.nekocloud.core.connector.bukkit.event.BukkitFilterResponseEvent;
import me.nekocloud.core.connector.bukkit.event.BukkitOnlineFetchEvent;
import me.nekocloud.core.io.ChannelWrapper;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.bukkit.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public final class BukkitHandler extends PacketHandler {

    BukkitConnector connector;
    ChannelWrapper channel;

    public BukkitHandler(
            final @NotNull BukkitConnector connector,
            final @NotNull ChannelWrapper channel
    ) {
        this.connector = connector;
        this.channel = channel;

        this.connector.setChannel(channel);
        this.connector.setActive(true);
    }

    @Override
    public void onDisconnect(final @NotNull ChannelWrapper channel) {
        this.connector.getLogger().info(this + " has disconnected.");
        this.connector.setActive(false);
        this.connector.onDisconnect();
    }

    @Override
    public void onExceptionCaught(final ChannelWrapper channel, final @NotNull Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void handle(final BukkitPlayerRedirect.@NotNull Error error) {
        final OnlineGamer gamer = GamerAPI.getOnline(error.getPlayerID());
        if (gamer != null && error.getReason() != null)
            gamer.sendMessage(error.getReason());
    }

    @Override
    public void handle(final @NotNull BukkitPlaySound packet) {
        val gamer = GamerAPI.getOnline(packet.getPlayerID());
        if (gamer == null)
            return;

        val player = Bukkit.getPlayer(gamer.getName());
        val soundType = packet.getSoundType();
        player.playSound(player.getLocation(),
                soundType.getSoundName(),
                packet.getVolume(),
                packet.getPitch());
    }

    @Override
    public void handle(final BukkitOnlineFetch.@NotNull Response onlineFetchResponse) {
        connector.getJavaPlugin().callEvent(new BukkitOnlineFetchEvent(
                onlineFetchResponse.getRegex(),
                onlineFetchResponse.getOnline()
        ));
        connector.setOnline(onlineFetchResponse.getRegex(), onlineFetchResponse.getOnline());
    }

    @Override
    public void handle(final BukkitServerInfoFilter.@NotNull Response response) {
        connector.getJavaPlugin().callEvent(new BukkitFilterResponseEvent(
                response.getRegex(),
                response.getFilter(),
                response.getServersInfos()
        ));
        connector.setServerInfo(response.getResponseId(), response.getServersInfos());
    }

    @Override
    public void handle(final @NotNull BukkitCommandExecute commandExecute) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandExecute.getCommand());
    }

    @Override
    public void handle(final @NotNull BukkitNetworking networking) {
        val gamer = GamerAPI.getById(networking.getPlayerID());
        if (gamer == null)
            return;

        val moneySection = gamer.getSection(MoneySection.class);
        val networkSection = gamer.getSection(NetworkingSection.class);

        switch (networking.getNetworkingType()) {
            case MONEY -> moneySection.updateMoneyForCore(
                    PurchaseType.getType(networking.getTypeValue()),
                    networking.getValue(),
                    false
            );
            case KEYS -> networkSection.updateKeys(
                    KeyType.getKey(networking.getTypeValue()),
                    networking.getValue(),
                    false
            );
        }
    }

    @Override
    public void handle(final @NotNull BukkitGroupPacket groupPacket) {
        val gamer = GamerAPI.getById(groupPacket.getPlayerID());
        if (gamer == null)
            return;

        gamer.setGroup(Group.getGroupByLevel(groupPacket.getGroupLevel()), false);
    }

    @Override
    public void handle(final @NotNull BukkitServerAction serverAction) {
        switch (serverAction.getAction()) {
            case RESTART -> Bukkit.shutdown();
            case LANG_RELOAD -> Language.reloadAll();
        }
    }

    @Override
    public String toString() {
        return "[" + channel.getRemoteAddress().getAddress().getHostAddress() + "] <-> PacketHandler";
    }
}
