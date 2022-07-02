package me.nekocloud.core.api.connection;

import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.Bungee;

import java.net.SocketAddress;

public interface ConnectionHandler {

    boolean handleBukkit(final Bukkit bukkit, final SocketAddress address);

    boolean handleBungee(final Bungee bungee, final SocketAddress address);

    void handlePlayer(final CorePlayer corePlayer);

    void handleBungeeDisconnection(final Bungee bungee);

    void handleBukkitDisconnection(final Bukkit bukkit);

    void handlePlayerDisconnection(final int id);

    void handlePlayerDisconnection(final CorePlayer player);
}
