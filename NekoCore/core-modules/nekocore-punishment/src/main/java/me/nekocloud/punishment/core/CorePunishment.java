package me.nekocloud.punishment.core;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.core.api.scheduler.CommonScheduler;
import me.nekocloud.core.io.packet.PacketProtocol;
import me.nekocloud.punishment.core.command.*;
import me.nekocloud.punishment.core.listener.PunishmentListener;
import me.nekocloud.punishment.data.BungeePunishmentAction;

import java.util.concurrent.TimeUnit;


//TODO @xwhilds: отправлять сообщение при снятии/выдаче наказаний в беседу вк, дс со стаффом

/**
 * @author xwhilds
 */
@CoreModuleInfo(name = "CorePunishment", author = "xwhilds")
public class CorePunishment extends CoreModule {

    protected CommonScheduler punishmentCleaner;

    @Override
    protected void onEnable() {
        loadPunishmentCleaner();

        getManagement().registerCommand(new KickCommand());
        getManagement().registerCommand(new BanCommand());
        getManagement().registerCommand(new MassBanCommand());
        getManagement().registerCommand(new MuteCommand());
        getManagement().registerCommand(new UnbanCommand());
        getManagement().registerCommand(new UnmuteCommand());

        getManagement().registerCommand(new HistoryCommand());
        getManagement().registerCommand(new InfoCommand());

        getManagement().registerListener(new PunishmentListener());

        PacketProtocol.BUNGEE.getMapper().registerPacket(18, BungeePunishmentAction.Issue.class);
        PacketProtocol.BUNGEE.getMapper().registerPacket(19, BungeePunishmentAction.Clear.class);
    }

    @Override
    protected void onDisable() {
        if (punishmentCleaner != null)
            punishmentCleaner.cancel();
    }

    protected void loadPunishmentCleaner() {
        this.punishmentCleaner = new CommonScheduler("punishmentCleaner228") {

            @Override
            public void run() {
                PunishmentManager.INSTANCE.getPunishmentMap().clear();
            }
        };

        punishmentCleaner.runTimer(0, 15, TimeUnit.MINUTES);
    }

}
