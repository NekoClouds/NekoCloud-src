package pw.novit.nekocloud.bungee.gamer;

import lombok.NonNull;
import me.nekocloud.base.locale.Language;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;

import java.util.List;

public final class BungeeProxyServer implements BungeeEntity {

    String name = "proxy-1";

    @Override
    public void sendMessage(String msg) {
        sendMessage(new TextComponent(msg));
    }

    @Override
    public void sendMessage(@NonNull BaseComponent component) {
        ProxyServer.getInstance().getConsole().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        sendMessage(new TextComponent(components));
    }

    @Override
    public Language getLanguage() {
        return Language.DEFAULT;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getChatName() {
        return name;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public void sendMessageLocale(String key, Object... replaced) {
        this.sendMessage(getLanguage().getMessage(key, replaced));
    }

    @Override
    public void sendMessagesLocale(String key, Object... replaced) {
        List<String> messages = getLanguage().getList(key, replaced);
        messages.forEach(this::sendMessage);
    }
}
