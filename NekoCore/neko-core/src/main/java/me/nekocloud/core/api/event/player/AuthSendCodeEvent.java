package me.nekocloud.core.api.event.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.event.Event;

import javax.annotation.Nullable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthSendCodeEvent extends Event {
    int playerID;
    CodeType codeType;

    @Getter
    @Nullable
    @FieldDefaults(level = PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    public enum CodeType {
        VK(0),
        DISCORD(1),
        TELEGRAM(2),
        ;

        int id;

        public static final CodeType[] VALUES = values();

        public static CodeType getType(final int id) {
            return id < 0 || id >= VALUES.length ? VK : VALUES[id];
        }
    }
}
