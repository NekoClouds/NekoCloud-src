package me.nekocloud.vkbot.api.objects.keyboard;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButton;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.KeyboardButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class Keyboard {

    private final Message message;

    private boolean oneTime, inline;

    public Keyboard(@NotNull Message message) {
        this.message = message;
    }

    private final Int2ObjectMap<List<KeyboardButton>> buttons = new Int2ObjectOpenHashMap<>();

    public Keyboard oneTime(boolean oneTime) {
        this.oneTime = oneTime;
        return this;
    }

    public Keyboard oneTime() {
        return oneTime(true);
    }

    public Keyboard inline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public Keyboard inline() {
        return inline(true);
    }

    public Keyboard button(int line, @NotNull KeyboardButtonAction action) {
        addButton(line, new KeyboardButton(this, null, action));
        return this;
    }

    public Keyboard button(@NotNull KeyboardButtonColor color, int line, @NotNull KeyboardButtonAction action) {
        addButton(line, new KeyboardButton(this, color, action));
        return this;
    }

    public KeyboardButton button(int line) {
        KeyboardButton button = new KeyboardButton(this);
        addButton(line, button);

        return button;
    }

    private void addButton(int line, @NotNull KeyboardButton button) {
        this.buttons.putIfAbsent(line, new ArrayList<>());
        this.buttons.get(line).add(button);
    }

    public Message message() {
        return message;
    }

}
