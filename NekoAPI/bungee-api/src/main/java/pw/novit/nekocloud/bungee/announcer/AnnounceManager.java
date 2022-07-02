package pw.novit.nekocloud.bungee.announcer;

import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.gamer.constans.SettingsType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public class AnnounceManager {
    Configuration config;
    ScheduledTask task;

    public void load() {
        if (task != null) task.cancel();

        config = loadConfig();
        startAnnounceTask(config.getInt("announcer_period"));
    }

    private @NotNull LinkedList<AnnounceMessage> getAnnounceMessages() {
        LinkedList<AnnounceMessage> announceMessages = new LinkedList<>();
        for (val announceMessageKey : config.getSection("announcer").getKeys()) {
            val messageSection = config.getSection("announcer").getSection(announceMessageKey);

//            val messageKey = messageSection.getString("message_key");
//            val hoverKey = messageSection.getString("hover_key");
            val hoverKey = announceMessageKey + "_HOVER";

            ClickEvent.Action clickAction = null;
            String clickActionContext = null;

            if (messageSection.get("click") != null) {
                clickAction = ClickEvent.Action.valueOf(
                        messageSection.getString("click.action"));

                clickActionContext = messageSection.getString("click.context");
            }
            announceMessages.add(new AnnounceMessage(announceMessageKey, hoverKey, clickAction, clickActionContext));
//            announceMessages.add(new AnnounceMessage(messageKey, hoverKey, clickAction, clickActionContext));
        }

        return announceMessages;
    }

    public void startAnnounceTask(int periodInMinutes) {
        val announceMessages = getAnnounceMessages();

        if (announceMessages.isEmpty())
            return;

        task = NekoBungeeAPI.getInstance()
                .getProxy()
                .getScheduler()
                .schedule(NekoBungeeAPI.getInstance(), new Runnable() {
            private int messageCounter = 0;

            @Override
            public void run() {
                val announceMessage = announceMessages.get(messageCounter);
                for (val player : ProxyServer.getInstance().getPlayers())
                    announceMessage.sendTo(player);

                ++messageCounter;
                if (messageCounter >= announceMessages.size()) {
                    messageCounter = 0;
                }}

        }, periodInMinutes, periodInMinutes, TimeUnit.MINUTES);
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = PRIVATE, makeFinal = true)
    private static class AnnounceMessage {
        String messageKey;
        String hoverKey;
        ClickEvent.Action clickAction;
        String clickActionContext;

        private void sendTo(ProxiedPlayer player) {
            if (messageKey == null || player.getServer().getInfo().getName().startsWith("auth"))
                return;

            val gamer = BungeeGamer.getGamer(player);
            if (gamer == null || !gamer.getSetting(SettingsType.AUTO_MESSAGE_ANNOUNCE))
                return;


            val lang = gamer.getLanguage();
            val componentBuilder = new ComponentBuilder(
                    Joiner.on("\n").join(lang.getList(messageKey)));

            if (clickAction != null && clickActionContext != null) {
                componentBuilder.event(new ClickEvent(clickAction, clickActionContext));
            }

            if (hoverKey != null) {
                componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(lang.getMessage(hoverKey))));
            }

            player.sendMessage(ChatMessageType.CHAT, componentBuilder.create());

        }
    }

    private Configuration loadConfig() {
        val config = new File(NekoBungeeAPI.getInstance().getDataFolder(), "announce.yml");
        Configuration cfg = null;
        try {
            if (!config.exists()) {
                Files.copy(NekoBungeeAPI.getInstance().getResourceAsStream("announce.yml"), config.toPath());
            }
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cfg;
    }
}

