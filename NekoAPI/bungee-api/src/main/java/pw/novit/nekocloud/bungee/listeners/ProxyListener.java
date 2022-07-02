package pw.novit.nekocloud.bungee.listeners;


import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class ProxyListener<T extends Plugin> implements Listener {

    protected final T plugin;

    protected ProxyListener(T plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    public void unregister() {
        ProxyServer.getInstance().getPluginManager().unregisterListener(this);
    }

}
