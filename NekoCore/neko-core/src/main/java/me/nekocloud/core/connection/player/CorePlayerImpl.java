package me.nekocloud.core.connection.player;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.GamerBase;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.base.gamer.sections.*;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatMessageType;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.chat.serializer.ComponentSerializer;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.command.CommandSendingType;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.player.IOfflineData;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.api.event.player.RedirectEvent;
import me.nekocloud.core.connection.player.offline.OfflineData;
import me.nekocloud.core.io.packet.bukkit.BukkitPlaySound;
import me.nekocloud.core.io.packet.bungee.BungeePlayerKick;
import me.nekocloud.core.io.packet.bungee.BungeePlayerMessage;
import me.nekocloud.core.io.packet.bungee.BungeePlayerRedirect;
import me.nekocloud.core.io.packet.bungee.BungeePlayerTitle;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CorePlayerImpl extends GamerBase implements CorePlayer, CommandSender {

    static String GET_LAST_ONLINE_QUERY = "SELECT `last_online` FROM `join_info` WHERE `id` = ? LIMIT 1;";
    static ImmutableSet<Class<? extends Section>> LOADED_SECTIONS = ImmutableSet.of(
            MoneySection.class,
            NetworkingSection.class,
            SettingsSection.class,
            FriendsSection.class
    ); // TODO переписать загрузку секций

    CommandSendingType sendingType      = CommandSendingType.PLAYER;
    IOfflineData offlineData            = new OfflineData(this);

    InetAddress ip;
    Bungee bungee;
    Version version;

    long joinedTime;

    @NonFinal long lastOnline = -1;
    @NonFinal Bukkit bukkit;

    public CorePlayerImpl(
            final String name, final String ip,
            final Bungee bungee, final int protocolVersion
    ) throws UnknownHostException {
        super(name);

        this.ip = InetAddress.getByName(ip);
        this.version = Version.getVersion(protocolVersion);
        this.bungee = bungee;
        this.joinedTime = System.currentTimeMillis();
    }

    @SneakyThrows
    public CorePlayerImpl(final String name, final String ip, final Bungee bungee) throws UnknownHostException {
        this(name, ip, bungee, -1);
    }

    @Override
    protected final ImmutableSet<Class<? extends Section>> initSections() {
        return LOADED_SECTIONS;
    }

    @Override
    public final int getMoney(final PurchaseType type) {
        return getSection(MoneySection.class).getMoney(type);
    }

    @Override
    public final void playSound(final SoundType sound) {
        playSound(sound, sound.getVolume(), sound.getPitch());
    }

    @Override
    public final void playSound(final SoundType sound, float volume, float pitch) {
        ensureOnline();

        bukkit.sendPacket(new BukkitPlaySound(true, getPlayerID(), sound, volume, pitch));
    }

    @Override
    public final void sendTitle(
            final BungeePlayerTitle.Action action, final String message,
            int fadeIn, int stay, int fadeOut
    ) {
        sendTitle(action, TextComponent.fromLegacyText(message), fadeIn, stay, fadeOut);
    }

    @Override
    public final void sendTitle(
            final BungeePlayerTitle.Action action, final BaseComponent[] message,
            int fadeIn, int stay, int fadeOut
    ) {
        bungee.sendPacket(new BungeePlayerTitle(
                getPlayerID(),
                action,
                ComponentSerializer.toString(message),
                fadeIn,
                stay,
                fadeOut
        ));
    }

    @Override
    public final void sendMessage(final @NotNull String message) {
        sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
    }

    @Override
    public final void sendMessage(
            final @NotNull ChatMessageType messageType,
            final BaseComponent[] baseComponents
    ) {
        bungee.sendPacket(new BungeePlayerMessage(getPlayerID(),
                messageType.ordinal(),
                new String[]{baseComponents}
        ));
    }

    @Override
    public final void sendTitle(String title, String subTitle) {
        sendTitle(title, subTitle, 5, 20, 10);
    }

    @Override
    public final void sendTitle(String title, String subTitle, long fadeIn, long stay, long fadeOut) {
        sendTitle(BungeePlayerTitle.Action.TITLE, title, (int) fadeIn, (int) stay, (int) fadeOut);
        sendTitle(BungeePlayerTitle.Action.SUBTITLE, subTitle, (int) fadeIn, (int) stay, (int) fadeOut);
    }

    @Override
    public final void sendActionBar(String message) {
        sendTitle(BungeePlayerTitle.Action.ACTIONBAR, message, 0, 0, 0);
    }

    @Override
    public final void sendActionBarLocale(String key, Object... replaced) {
        sendActionBar(getLanguage().getMessage(key, replaced));
    }

    @Override
    public final boolean isHuman() {
        return true;
    }

    @Override
    public final boolean isOnline() {
        return this.bungee != null;
    }

    @Override
    public final void redirect(final @NotNull Bukkit bukkit) {
        RedirectEvent event = NekoCore.getInstance().callEvent(new RedirectEvent(
                this, this.bukkit, bukkit
        ));

        if (event.isCancelled())
            return;

        this.sendMessageLocale("SERVER_QUEUE", event.getTo().getName());
        this.bungee.sendPacket(new BungeePlayerRedirect(getPlayerID(), event.getTo().getName()));
    }

    @Override
    public final void setBukkit(final @NotNull Bukkit bukkit) {
        if (this.bukkit != null) {
            this.bukkit.removePlayer(this);
        }

        this.bukkit = bukkit;
        this.bukkit.addPlayer(this);
    }

    @Override
    public final void setGroup(final Group group, boolean mysql) {
        super.setGroup(group, isOnline());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getPlayerID(), getName());
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof CorePlayerImpl other)) {
            return false;
        }

        return Objects.equal(getPlayerID(), other.getPlayerID())
                && Objects.equal(getName(), other.getName());
    }

    @Override
    public final void redirect(final @NotNull String serverName) {
        redirect(NekoCore.getInstance().getBukkit(serverName));
    }

    @Override
    public final void disconnect(final BaseComponent... reason) {
        ensureOnline();

        bungee.sendPacket(new BungeePlayerKick(getPlayerID(), ComponentSerializer.toString(reason)));
    }

    @Override
    public final long getLastOnline() {
        if (lastOnline < 0) {
            lastOnline = CoreSql.getDatabase().executeQuery(GET_LAST_ONLINE_QUERY, (rs) -> {
                if (rs.next())
                    return (lastOnline = rs.getLong("last_online"));

                return 0L;
            }, getPlayerID());
        }

        return lastOnline;
    }
}
