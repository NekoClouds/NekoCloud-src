package me.nekocloud.core.connector.bungee;

import lombok.Getter;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.connector.bungee.listeners.ConnectorListener;
import me.nekocloud.core.connector.bungee.listeners.ProxyPingListener;
import net.md_5.bungee.api.plugin.Plugin;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;

@Getter
public class BungeeConnectorPlugin extends Plugin {

    private BungeeConnector connector;

    @Override
    public void onLoad() {
        Language.reloadAll();
    }

    @Override
    public void onEnable() {
        connector = new BungeeConnector(this);
        CoreConnector.setInstance(connector);

        connector.start();
        new ConnectorListener(this, connector);
        new ProxyPingListener(NekoBungeeAPI.getInstance(), connector);
        new ShittyTab(this, connector);
    }

    @Override
    public void onDisable() {
        connector.shutdown();
    }
}
