package me.nekocloud.core.discord;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.core.discord.bot.DiscordBot;

@CoreModuleInfo(name = "DiscordBot", author = "_Novit_", depends = "Webmodule")
public class CoreDiscord extends CoreModule {

    private DiscordBot discordBot;

    @Override
    protected void onEnable() {
        discordBot = new DiscordBot(this);
    }

    @Override
    protected void onDisable() {
        discordBot.unregisterCommands();
        discordBot.getJda().shutdownNow();
    }

}
