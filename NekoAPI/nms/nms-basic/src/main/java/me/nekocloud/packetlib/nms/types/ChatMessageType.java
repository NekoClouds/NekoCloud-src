package me.nekocloud.packetlib.nms.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatMessageType {
    CHAT((byte)0),
    SYSTEM((byte)1),
    GAME_INFO((byte)2);

    @Deprecated
    private final byte chatType; //на 1.12 не нужно
}
