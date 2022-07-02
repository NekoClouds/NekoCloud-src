package me.nekocloud.myserver;

import lombok.Getter;
import lombok.SneakyThrows;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.myserver.command.MyServerCommand;
import me.nekocloud.myserver.listener.MyServerListener;
import me.nekocloud.myserver.type.MyServerManager;
import me.nekocloud.myserver.type.MyServerType;
import me.nekocloud.myserver.type.PlayerMyServer;

import java.nio.file.Files;

@CoreModuleInfo(name = "NekoMyServer", author = "iStonlexx")
public class NekoMyServer extends CoreModule {

    @Getter
    private static NekoMyServer instance; {
        instance = this;
    }

    @Override
    protected void onEnable() {
        createServersDirectories();

        getManagement().registerListener(new MyServerListener());
        getManagement().registerCommand(new MyServerCommand());
    }

    @Override
    protected void onDisable() {
        for (PlayerMyServer playerMyServer : MyServerManager.INSTANCE.getActiveServers()) {
            playerMyServer.shutdown();
        }
    }


    @SneakyThrows
    private void createServersDirectories() {
        for (MyServerType myServerType : MyServerType.SERVER_TYPES) {

            if (!Files.exists(myServerType.getServersFolder()))
                Files.createDirectories(myServerType.getServersFolder());
        }

        Files.createDirectories(getModuleFolder().toPath().resolve("RunningServers"));
    }

}
