package me.nekocloud.nekoapi.listeners;

import com.google.common.io.ByteStreams;
import lombok.val;
import me.nekocloud.api.event.gamer.GamerChangeGroupEvent;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerChangePrefixEvent;
import me.nekocloud.api.event.gamer.GamerChangeSettingEvent;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.SkinUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public final class BungeeMessageListener extends DListener<NekoAPI> implements PluginMessageListener {

    public BungeeMessageListener(final NekoAPI nekoAPI) {
        super(nekoAPI);

        Bukkit.getMessenger().registerIncomingPluginChannel(
                nekoAPI, "nekoapi:skins", this);

        initOutgoingChannel("nekoapi:group");
        initOutgoingChannel("nekoapi:language");
        initOutgoingChannel("nekoapi:prefix");
        initOutgoingChannel("nekoapi:settings");
    }

    private void initOutgoingChannel(final String name) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(javaPlugin, name);
    }

    @EventHandler
    public void onChangeGroup(final @NotNull GamerChangeGroupEvent e) {
        val player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        val name = player.getName();
        val groupID = e.getGroup().getId();

        val out = ByteStreams.newDataOutput();
        out.writeUTF(name);
        out.writeInt(groupID);

        player.sendPluginMessage(javaPlugin, "nekoapi:group", out.toByteArray());
    }

    @EventHandler
    public void onChangeLang(final @NotNull GamerChangeLanguageEvent e) {
        val player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        val out = ByteStreams.newDataOutput();
        out.writeUTF(player.getName());
        out.writeInt(e.getLanguage().getId());
        out.writeInt(e.getOldLanguage().getId());

        player.sendPluginMessage(javaPlugin, "nekoapi:language", out.toByteArray());
    }

    @EventHandler
    public void onChangePrefix(final @NotNull GamerChangePrefixEvent e) {
        val player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        val name = player.getName();
        val prefix = e.getPrefix();

        val out = ByteStreams.newDataOutput();
        out.writeUTF(name);
        out.writeUTF(prefix);

        player.sendPluginMessage(javaPlugin, "nekoapi:prefix", out.toByteArray());
    }

    @EventHandler
    public void onChangeSettings(final @NotNull GamerChangeSettingEvent e) {
        val player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        val name = player.getName();
        val settingID = e.getSetting().getKey();
        val result = e.isResult();

        val out = ByteStreams.newDataOutput();
        out.writeUTF(name);
        out.writeInt(settingID);
        out.writeBoolean(result);

        player.sendPluginMessage(javaPlugin, "nekoapi:settings", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(final @NotNull String channel, Player player, byte[] bytes) {
        if (!channel.equals("nekoapi:skins")) {
            return;
        }

        val input = ByteStreams.newDataInput(bytes);

        val target = Bukkit.getPlayer(input.readUTF());
        if (target == null || !player.isOnline()) {
            return;
        }

        SkinUtil.setSkin(
                target,
                input.readUTF(), input.readUTF(), input.readUTF(),
                SkinType.getSkinType(input.readInt()), true
        );
    }
}
