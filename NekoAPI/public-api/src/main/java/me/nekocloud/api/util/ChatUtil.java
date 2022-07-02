package me.nekocloud.api.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

@UtilityClass
public class ChatUtil {

    public BaseComponent getComponentFromList(List<String> list) {
        int size = list.size();
        TextComponent components = new TextComponent();
        for (int i = 0; i < size; i++) {
            components.addExtra(list.get(i));
            if (i + 1 < size) {
                components.addExtra("\n");
            }

        }
        return components;
    }
}
