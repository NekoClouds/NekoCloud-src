package me.nekocloud.party.core;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.party.core.command.PartyChatCommand;
import me.nekocloud.party.core.command.PartyCommand;
import me.nekocloud.party.core.listener.PartyWarpListener;

@CoreModuleInfo(name = "CoreParty", author = "_Novit_")
public class CoreParty extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new PartyCommand());
        getManagement().registerCommand(new PartyChatCommand());

        getManagement().registerListener(new PartyWarpListener());
    }

    @Override
    protected void onDisable() {}

}
