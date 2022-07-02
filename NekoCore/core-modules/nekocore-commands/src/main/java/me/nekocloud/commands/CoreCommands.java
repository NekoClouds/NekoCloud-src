package me.nekocloud.commands;

import me.nekocloud.commands.impl.*;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.core.common.group.command.GroupCommand;

@CoreModuleInfo(name = "CoreCommands", author = "_Novit_")
public class CoreCommands extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new FindCommand());
        getManagement().registerCommand(new AlertCommand());
        getManagement().registerCommand(new HubCommand());
        getManagement().registerCommand(new ServerCommand());
        getManagement().registerCommand(new GroupCommand());
        getManagement().registerCommand(new OnlineCommand());
        getManagement().registerCommand(new SendCommand());
        getManagement().registerCommand(new InfoCommand());
        getManagement().registerCommand(new WatchCommand());
        getManagement().registerCommand(new DomianStatsCommand());
        getManagement().registerCommand(new IpCommand());
    }

    @Override
    protected void onDisable() { }

}
