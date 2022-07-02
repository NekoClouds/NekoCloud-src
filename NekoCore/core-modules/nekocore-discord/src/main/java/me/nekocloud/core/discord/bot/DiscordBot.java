package me.nekocloud.core.discord.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import me.nekocloud.core.api.config.Configuration;
import me.nekocloud.core.api.config.ConfigurationProvider;
import me.nekocloud.core.api.config.YamlConfiguration;
import me.nekocloud.core.discord.CoreDiscord;
import me.nekocloud.core.discord.api.DiscordAPI;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.command.HelpCommand;
import me.nekocloud.core.discord.command.TestCommand;
import me.nekocloud.core.discord.command.account.*;
import me.nekocloud.core.discord.command.feature.BotPingCommand;
import me.nekocloud.core.discord.command.feature.FindCommand;
import me.nekocloud.core.discord.command.feature.OnlineCommand;
import me.nekocloud.core.discord.api.handler.MessageHandler;
import me.nekocloud.core.discord.listeners.AuthDiscordCodeListener;
import me.nekocloud.core.discord.listeners.PlayerListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscordBot {


    @Getter
    private static DiscordBot instance;

    public static DiscordAPI API_INSTANCE                     = new DiscordAPI();
    public static Map<String, DiscordCommand> COMMAND_MAP     = new HashMap<>();

    private final CoreDiscord coreDiscord;
    private final Configuration config;
    private final JDA jda;

    @SneakyThrows
    public DiscordBot(CoreDiscord coreDiscord) {
        instance = this;
        this.coreDiscord = coreDiscord;

        config = loadConfig();

        jda = JDABuilder.createDefault(getConfig().getString("token"))
                .addEventListeners(new MessageHandler())
                .setAutoReconnect(true)
                .build();

        jda.getPresence().setPresence(Activity.playing("mc.NekoCloud.me"), true);


        registerCommands();
        registerListeners();
    }

    private void registerListeners() {
        coreDiscord.getManagement().registerListener(new PlayerListener());
        coreDiscord.getManagement().registerListener(new AuthDiscordCodeListener());
    }

    private void registerCommands() {
        registerCommand(new TestCommand());
        registerCommand(new OnlineCommand());
        registerCommand(new CoreInfoCommand());
        registerCommand(new HelpCommand());
        registerCommand(new BotPingCommand());
        registerCommand(new FindCommand());

        /* аккаунт */
        registerCommand(new AccountLinkCommand());
        registerCommand(new AccountUnLinkCommand());
        registerCommand(new AccountInfoCommand());
        registerCommand(new AccountRecoveryCommand());
        registerCommand(new TwoFactorCallbackCommand(true));
        registerCommand(new TwoFactorCallbackCommand(false));
        registerCommand(new TwoFactorModeCommand());

    }


    // неебет
    public void registerCommand(DiscordCommand command) {
        command.aliases.forEach(msg -> COMMAND_MAP.put(msg, command));
    }

    public void unregisterCommands() {
        COMMAND_MAP.clear();
    }

    private Configuration loadConfig() {
        coreDiscord.saveResource("config.yml");
        val config = new File(getCoreDiscord().getModuleFolder(), "config.yml");
        Configuration cfg = null;
        try {
            if (!config.exists()) {
                Files.copy(getCoreDiscord().getResourceAsStream("config.yml"), config.toPath());
            }
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cfg;
    }

}
