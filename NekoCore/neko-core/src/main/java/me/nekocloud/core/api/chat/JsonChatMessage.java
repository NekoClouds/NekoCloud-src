package me.nekocloud.core.api.chat;

import me.nekocloud.base.locale.Language;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.chat.component.ComponentBuilder;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.chat.event.ClickEvent;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record JsonChatMessage(ComponentBuilder componentBuilder) {

    public static JsonChatMessage create() {
        return new JsonChatMessage(new ComponentBuilder(""));
    }

    public static JsonChatMessage create(final @NotNull String text) {
        return new JsonChatMessage(new ComponentBuilder(text));
    }

    public static JsonChatMessage createLocalized(final @NotNull Language lang, @NotNull String key) {
        return new JsonChatMessage(new ComponentBuilder(lang.getMessage(key)));
    }

    public JsonChatMessage addHover(final @NotNull HoverEvent.Action action, @NotNull String hover) {
        HoverEvent hoverEvent = new HoverEvent(action, TextComponent.fromLegacyText(hover));

        componentBuilder.event(hoverEvent);
        return this;
    }

    public JsonChatMessage addHoverLocalized(final @NotNull HoverEvent.Action action, @NotNull
            Language localizationResource, @NotNull String hoverKey) {
        HoverEvent hoverEvent = new HoverEvent(action,
                TextComponent.fromLegacyText(localizationResource.getMessage(hoverKey)));

        componentBuilder.event(hoverEvent);
        return this;
    }

    public JsonChatMessage addClick(final @NotNull ClickEvent.Action action, @NotNull String command) {
        ClickEvent clickEvent = new ClickEvent(action, command);

        componentBuilder.event(clickEvent);
        return this;
    }


    public JsonChatMessage addText(final @NotNull String text) {
        componentBuilder.appendLegacy(text);
        return this;
    }

    public JsonChatMessage addTextLocalized(final @NotNull Language localizationResource, @NotNull String key) {
        componentBuilder.appendLegacy(localizationResource.getMessage(key));
        return this;
    }


    public JsonChatMessage addJoiner(final @NotNull ComponentBuilder.Joiner joiner) {
        componentBuilder.append(joiner);
        return this;
    }

    public JsonChatMessage addColor(final @NotNull ChatColor chatColor) {
        componentBuilder.append(chatColor.toString());
        return this;
    }

    public JsonChatMessage setColor(final @NotNull ChatColor chatColor) {
        componentBuilder.color(chatColor);
        return this;
    }

    public JsonChatMessage addComponents(BaseComponent... baseComponents) {
        componentBuilder.append(baseComponents);
        return this;
    }


    public BaseComponent[] build() {
        return componentBuilder.create();
    }

    public void sendMessage(final @NotNull ChatMessageType chatMessageType, @NotNull CommandSender commandSender) {
        commandSender.sendMessage(chatMessageType, build());
    }

    public void sendMessage(final @NotNull CommandSender commandSender) {
        sendMessage(ChatMessageType.CHAT, commandSender);
    }

}
