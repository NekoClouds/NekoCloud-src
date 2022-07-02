package me.nekocloud.reports;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.reports.command.ReportListCommand;
import me.nekocloud.reports.command.ReportSendCommand;

@CoreModuleInfo(name = "CoreReports", author = "_Novit_")
public class CoreReports extends CoreModule {

    @Override
    protected void onEnable() {
        getManagement().registerCommand(new ReportSendCommand());
        getManagement().registerCommand(new ReportListCommand());
    }

    @Override
    protected void onDisable() {}
}
