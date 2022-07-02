package me.nekocloud.streams;

import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.streams.command.StreamCommand;
import me.nekocloud.streams.command.StreamsCommand;

@CoreModuleInfo(name = "CoreStreams", author = "iStonlexx")
public class CoreStreams extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new StreamCommand());
        getManagement().registerCommand(new StreamsCommand());
    }

    @Override
    protected void onDisable() {
        NekoCore.getInstance().getSchedulerManager().cancelScheduler("streams_update");
    }

}
