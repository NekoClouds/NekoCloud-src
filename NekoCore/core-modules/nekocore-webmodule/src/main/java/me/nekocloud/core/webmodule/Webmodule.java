package me.nekocloud.core.webmodule;

import lombok.Getter;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;

@Getter
@CoreModuleInfo(name = "Webmodule", author = "_Novit_") // TODO: накодить
public class Webmodule extends CoreModule {

    @Override
    protected void onEnable() {
//        new DomianStats(getCore(), this);
    }

    @Override
    protected void onDisable() {

    }


}
