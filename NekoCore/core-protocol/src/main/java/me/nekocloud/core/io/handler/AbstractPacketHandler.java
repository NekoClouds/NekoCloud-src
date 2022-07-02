package me.nekocloud.core.io.handler;

import me.nekocloud.core.io.packet.bukkit.*;
import me.nekocloud.core.io.packet.bungee.*;
import me.nekocloud.core.io.packet.handshake.Handshake;

public abstract class AbstractPacketHandler {

    public void handle(Handshake handshake) {
    }

    public void handle(BungeePlayerLogin playerLogin) {
    }

    public void handle(BungeePlayerRedirect playerRedirect) {
    }

    public void handle(BungeePlayerJoin playerJoin) {
    }

    public void handle(BungeePlayerLogin.Result playerLoginResult) {
    }

    public void handle(BungeePlayerDisconnect playerDisconnect) {
    }

    public void handle(BungeePlayerSwitchServer playerSwitchServer) {
    }

    public void handle(BungeeOnlineUpdate onlineUpdate) {
    }

    public void handle(BungeePlayerTitle playerTitle) {
    }

    public void handle(BungeePlayerActionBar playerActionBar) {
    }

    public void handle(BungeePlayerActionBar.Announce announce) {
    }

    public void handle(BungeePlayerBossBar.Create create) {
    }

    public void handle(BungeePlayerBossBar.Remove remove) {
    }

    public void handle(BungeePlayerMessage playerMessage) {
    }

    public void handle(BungeePlayerMessage.Announce announce) {
    }

    public void handle(BungeePlayerDispatchCommand playerDispatchCommand) {
    }

    public void handle(BungeeCommandExecute commandExecute) {
    }

    public void handle(BukkitPlayerRedirect playerRedirect) {
    }

    public void handle(BukkitPlayerRedirect.Error error) {
    }

    public void handle(BungeeCommandRegister commandRegister) {
    }

    public void handle(BungeePlayerKick playerKick) {
    }

    public void handle(BungeeServerAction serverAction) {
    }

    public void handle(BukkitPlaySound playSound) {
    }

    public void handle(BukkitOnlineFetch onlineFetch) {
    }

    public void handle(BukkitOnlineFetch.Response onlineFetchResponse) {
    }

    public void handle(BukkitCommandExecute commandExecute) {
    }

    public void handle(BukkitNetworking networking) {
    }

    public void handle(BukkitGroupPacket groupPacket) {
    }

    public void handle(BukkitSetting setting) {
    }

    public void handle(BukkitSetting.Lang language) {
    }

    public void handle(BukkitSetting.Prefix prefix) {
    }

    public void handle(BukkitPlayerDispatchCommand playerDispatchCommand) {
    }

    public void handle(BukkitServerInfo serverInfo) {
    }

    public void handle(BukkitServerInfoFilter.Request serverInfoFilterRequest) {
    }

    public void handle(BukkitServerInfoFilter.Response serverInfoFilterResponse) {
    }

    public void handle(BukkitServerAction restartPacket) {
    }
}
