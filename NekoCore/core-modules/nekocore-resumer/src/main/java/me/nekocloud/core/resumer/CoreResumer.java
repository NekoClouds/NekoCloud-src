package me.nekocloud.core.resumer;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;

@CoreModuleInfo(name = "CoreResumer", author = "_Novit_")
public class CoreResumer extends CoreModule { //TODO накодить

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new RejoinCommand());
    }

    @Override
    protected void onDisable() {

    }
}
