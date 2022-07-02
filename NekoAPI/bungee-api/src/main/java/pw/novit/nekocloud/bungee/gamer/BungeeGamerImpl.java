package pw.novit.nekocloud.bungee.gamer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.GamerBase;
import me.nekocloud.base.gamer.constans.Version;
import me.nekocloud.base.gamer.sections.SettingsSection;
import me.nekocloud.base.gamer.sections.SkinSection;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.MySqlDatabase;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.api.event.gamer.GamerChangeLanguageEvent;
import pw.novit.nekocloud.bungee.api.event.player.PlayerChangeSkinEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.api.utils.BungeeUtil;
import pw.novit.nekocloud.bungee.api.utils.SkinUtil;

import java.net.InetAddress;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public final class BungeeGamerImpl extends GamerBase implements BungeeEntity, BungeeGamer {

    final MySqlDatabase MY_SQL_DATABASE = GlobalLoader.getMysqlDatabase();
    final InetAddress inetAddress;
    InetAddress lastIp;

    @Setter
    boolean waitLicense;

    String lastServer;
    long lastOnline;

    @Setter
    boolean coreLogged;

    @Setter
    boolean saved = true;

    public BungeeGamerImpl(final String name, final @NotNull InetAddress inetAddress) {
        super(name);

        this.inetAddress = inetAddress;
        MY_SQL_DATABASE.executeQuery("SELECT * FROM `join_info` WHERE `id` = ? LIMIT 1;", (rs) -> {
            if (rs.next()) {
                this.lastServer = rs.getString("server");
                this.lastOnline = rs.getLong("last_online");
                this.lastIp = InetAddress.getByName(rs.getString("ip"));
            } else {
                this.lastServer = "unknown";
                this.lastOnline = 0;
                this.lastIp = InetAddress.getByName("0.0.0.0");
                MY_SQL_DATABASE.execute("INSERT INTO `join_info` (`id`, `ip`, `server`, `last_online`) VALUES (?, ?, ?, ?)",
                        getPlayerID(), "0.0.0.0", lastServer, lastOnline);
            }
            return Void.TYPE;
        }, getPlayerID());
    }

    public void disconnect() {
        val player = getPlayer();
        if (player == null)
            return;

        val sessionIp = inetAddress.getHostAddress();
        val server = player.getServer();

        lastServer = server == null ? "null" : server.getInfo().getName();

        MY_SQL_DATABASE.execute("UPDATE `join_info` SET `ip` = ?, `server` = ?, `last_online` = ? WHERE `id` = ?;",
                sessionIp, lastServer, System.currentTimeMillis(), getPlayerID());

        // Чистим кеш
        remove();
    }

    @Override
    public void updateSkin(final String skinName) {
        BungeeUtil.submitAsync(() -> {
            val player = getPlayer();

            getSection(SkinSection.class).updateSkinName(skinName);
            val setterSkin = SkinUtil.setSkin(player.getPendingConnection(),
                    GlobalLoader.getSkin(skinName),
                    player.getServer());

            val skinEvent = new PlayerChangeSkinEvent(player, setterSkin);
            BungeeUtil.callEvent(skinEvent);
        });
    }

    @Override
    public void sendTitle(Title title) {
        val player = getPlayer();
        if (player == null || title == null)
            return;

        player.sendTitle(title);
    }

    @Override
    public ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(getName());
    }

    @Override
    public void sendMessage(BaseComponent component) {
        val player = ProxyServer.getInstance().getPlayer(getName());
        if (player != null && player.isConnected())
            player.sendMessage(component);

    }

    @Override
    public void sendMessage(String message) {
        val textComponent = new TextComponent(message);
        sendMessage(textComponent);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        val player = ProxyServer.getInstance().getPlayer(getName());
        if (player != null && player.isConnected())
            player.sendMessage(components);
    }

    @Override
    public void sendTitle(String title, String subTitle) {
        sendTitle(BungeeCord.getInstance().createTitle()
                .title(new TextComponent(title))
                .subTitle(new TextComponent(subTitle)));
    }

    @Override
    public void sendTitle(String title, String subTitle, long fadeIn, long stay, long fadeOut) {
        sendTitle(BungeeCord.getInstance().createTitle()
                .title(new TextComponent(title))
                .subTitle(new TextComponent(subTitle))
                .fadeIn((int) fadeIn)
                .fadeOut((int) fadeOut)
                .stay((int) stay));
    }

    @Override
    public void sendActionBar(String message) {
        val player = getPlayer();
        if (player == null)
            return;

        player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public void sendActionBarLocale(String key, Object... replaced) {
        sendActionBar(this.getLanguage().getMessage(key, replaced));
    }

    @Override
    public Version getVersion() {
        val player = getPlayer();
        if (player == null)
            return Version.EMPTY;

        return Version.getVersion(player.getPendingConnection().getVersion());
    }

    @Override
    public InetAddress getIp() {
        return inetAddress;
    }

    @Override
    public @NotNull String getChatName() {
        return "§r" + getPrefix() + getName();
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public void setLanguage(Language language) {
        if (getPlayer() == null)
            return;

        val oldLanguage = this.getLanguage();
        if (oldLanguage == language)
            return;

        val event = new GamerChangeLanguageEvent(this, language, oldLanguage);
        BungeeUtil.callEventAsync(event);

        getSection(SettingsSection.class).setLang(language, true);
    }

    @Override
    public void disconnect(final @NotNull String reason) {
        getPlayer().disconnect(new TextComponent(reason));
    }
}
