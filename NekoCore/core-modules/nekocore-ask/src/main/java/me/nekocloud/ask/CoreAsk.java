package me.nekocloud.ask;

import me.nekocloud.ask.command.AnsCommand;
import me.nekocloud.ask.command.AskCommand;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;

/**
 * ЕБУЧИЕ ВОПРОСЫ
 */
@CoreModuleInfo(name = "CoreAsk", author = "_Novit_")
public class CoreAsk extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new AnsCommand());
        getManagement().registerCommand(new AskCommand());
    }

    @Override
    protected void onDisable() {}
}
