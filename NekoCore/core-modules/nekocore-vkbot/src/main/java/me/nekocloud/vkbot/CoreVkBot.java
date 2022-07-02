package me.nekocloud.vkbot;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.core.api.scheduler.CommonScheduler;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.*;
import me.nekocloud.vkbot.command.account.*;
import me.nekocloud.vkbot.command.admin.*;
import me.nekocloud.vkbot.command.feature.*;
import me.nekocloud.vkbot.listener.AuthVkCodeListener;
import me.nekocloud.vkbot.listener.CoreListener;
import me.nekocloud.vkbot.listener.PlayerGroupVKListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@CoreModuleInfo(name = "VkBot", author = "_Novit_", depends = "CoreReports")
public class CoreVkBot extends CoreModule {

    private final String VKBOT_TASK_PATTERN = "vkbot-reconnect";
    private CommonScheduler reconnectTask;

    @Override
    protected void onEnable() {
        //  Господи и Владыка живота моего, дух праздности,
        //  уныния, любоначалия и празднословия не даждь ми.
        //  Дух же целомудрия, смиренномудрия, терпения и любве
        //  даруй ми, рабу Твоему. Ей, Господи, Царю,
        //  даруй ми зрети моя прегрешения и не осуждати брата моего, яко
        //  благословен еси во веки веков. Аминь.
        connectToVK();

        getManagement().registerListener(new AuthVkCodeListener());
        getManagement().registerListener(new CoreListener());
        getManagement().registerListener(new PlayerGroupVKListener());

        startReconnectTask();
    }

    @Override
    protected void onDisable() {
        VkBot.INSTANCE.stop();

        reconnectTask.cancel();
    }

    protected void startReconnectTask() {
        this.reconnectTask = new CommonScheduler(VKBOT_TASK_PATTERN) {

            @Override
            public void run() {
                VkBot.INSTANCE.stop();

                connectToVK();
            }
        };

        reconnectTask.runTimer(2, 2, TimeUnit.HOURS);
    }

    protected void connectToVK() {
        try {
            VkBot.INSTANCE.runCallbackApi();

            registerCommand(new ServerRestartCommand());
            registerCommand(new AlertCommand());
            registerCommand(new GroupCommand());
            registerCommand(new AdminRecoveryCommand());
            registerCommand(new PlayerAddressCommand());
            registerCommand(new ModuleCommand());
            registerCommand(new EconomyVirtsCommand());
            registerCommand(new EconomyCoinsCommand());

            registerCommand(new ByeByeCommand());
//            registerCommand(new OnlineStaffCommand());
//            registerCommand(new PunishmentBanCommand());
//            registerCommand(new PunishmentKickCommand());
//            registerCommand(new PunishmentMuteCommand());
//            registerCommand(new PunishmentUnbanCommand());

            registerCommand(new PhraseCommand());
            registerCommand(new TabCompleteCommand());
//            registerCommand(new ServerChatCommand());
            registerCommand(new OnlineCommand());
            registerCommand(new CheckNickCommand());
            registerCommand(new PlayerFindCommand());

            registerCommand(new ToCoreCommand());
            registerCommand(new CoreCommand());

            registerCommand(new TwoFactorCallbackCommand(true));
            registerCommand(new TwoFactorCallbackCommand(false));
            registerCommand(new TwoFactorModeCommand());
            registerCommand(new AccountLinkCommand());
            registerCommand(new AccountUnLinkCommand());
            registerCommand(new AccountInfoCommand());
            registerCommand(new AccountRecoveryCommand());

            registerCommand(new HelpCommand());
            registerCommand(new BotCommand());
            registerCommand(new StartCommand());
            registerCommand(new DelKeyboardCommand());
            registerCommand(new WriteCommand());

            registerCommand(new TestPlayer());
        }

        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void registerCommand(@NotNull VkCommand vkCommand) {
        VkBot.INSTANCE.registerCommand(vkCommand);
    }

}
