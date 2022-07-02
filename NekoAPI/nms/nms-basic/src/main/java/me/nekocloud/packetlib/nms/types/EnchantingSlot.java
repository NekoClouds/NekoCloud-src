package me.nekocloud.packetlib.nms.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnchantingSlot {
    TO_ENCHANT(0),
    LAPIS(1);

    private final int slot;
}
