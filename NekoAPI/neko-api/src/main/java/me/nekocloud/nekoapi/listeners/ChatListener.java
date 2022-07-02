package me.nekocloud.nekoapi.listeners;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.async.AsyncGamerChatFormatEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ChatUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class ChatListener extends DListener<NekoAPI> {

    private final Spigot spigot = NekoCloud.getGamerManager().getSpigot();

    public ChatListener(final NekoAPI nekoAPI) {
        super(nekoAPI);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncPlayerChat(final @NotNull AsyncPlayerChatEvent e) {
        val player = e.getPlayer();

        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        val suffix = gamer.getGroup().getSuffix();

        val prefix = gamer.getPrefix();
        e.setFormat(" §r" + prefix + player.getName() + suffix + " %2$s");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(final @NotNull AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        val sender = GAMER_MANAGER.getGamer(e.getPlayer());
        if (sender == null) {
            return;
        }

        Set<BukkitGamer> recipients = new HashSet<>();
        for (val player : e.getRecipients()) {
            val otherGamer = GAMER_MANAGER.getGamer(player);
            if (otherGamer == null) {
                continue;
            }

            recipients.add(otherGamer);
        }

        val event = new AsyncGamerChatFormatEvent(
                sender,
                recipients,
                e.getFormat(),
                e.getMessage());
        BukkitUtil.callEvent(event);

        val message = event.getMessage();
        spigot.sendMessage(event.getBaseFormat().replace("%2$s", "") + message);
        for (val gamer : event.getRecipients()) {
            val eventFormat = event.getFormat(gamer).replace("%2$s", "");

            gamer.sendMessage(eventFormat + message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatToPlayer(final @NotNull AsyncGamerChatFormatEvent e) {
        val gamerSender = e.getGamer();

        val suffix = gamerSender.getGroup().getSuffix();
        val color = ChatColor.getByChar(suffix.charAt(suffix.length() - 1));

        for (val gamer : new HashSet<>(e.getRecipients())) {
            if (gamer == gamerSender || !gamer.getSetting(SettingsType.CHAT)) {
                continue;
            }

            boolean nicknameFound = false;

            val finalComponent = new TextComponent(e.getFormat(gamer).replace("%2$s", ""));

            for (String word : e.getMessage().split(" ")) {
                if (!nicknameFound && word.equalsIgnoreCase(gamer.getName())) {
                    nicknameFound = true;

                    val component = new TextComponent(gamer.getName());
                    component.setColor(ChatColor.LIGHT_PURPLE);
                    component.setUnderlined(true);

                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatUtil.getComponentFromList(
                            gamer.getLanguage().getList("HOVER_MESSAGE_CHAT", gamerSender.getDisplayName(),
                                    gamerSender.getVersion(), gamerSender.getLanguage(), gamerSender.getLevelNetwork())))
                    }));

                    component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + gamerSender.getName() + " "));

                    finalComponent.addExtra(component);

                } else {
                    val textComponent = new TextComponent(word);
                    if (color != null) {
                        textComponent.setColor(color);
                    }
                    finalComponent.addExtra(textComponent);
                }

                finalComponent.addExtra(" ");
            }

            if (nicknameFound) {
                e.removeRecipient(gamer);

                gamer.playSound(SoundType.LEVEL_UP, 0.6f, 0.2f);

                gamer.sendMessage(finalComponent);
            }
        }
    }
}
