package me.nekocloud.base.gamer.constans;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.CommonWords;
import net.kyori.text.format.TextColor;

import javax.annotation.Nullable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum PurchaseType {
    COINS(0, TextColor.YELLOW, '6', CommonWords.COINS_1),
    VIRTS(1, TextColor.AQUA, 'd', CommonWords.VIRTS_1),
    ;

    static final Int2ObjectMap<PurchaseType> MONEY_TYPE = new Int2ObjectOpenHashMap<>();

    int id;
    TextColor textColor;

    char color;
    CommonWords commonWords;

    @Nullable
    public static PurchaseType getType(int id) {
        return MONEY_TYPE.get(id);
    }

    static {
        for (val purchaseType : values()) {
            MONEY_TYPE.put(purchaseType.id, purchaseType);
        }
    }
}