package me.nekocloud.base.skin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum SkinType {
    ELY(0),
    MOJANG(1),
    CUSTOM(2), //скин созданный по value, либо загруженный с БД нклауд
    ;

    static final Int2ObjectMap<SkinType> SKIN_TYPES = new Int2ObjectOpenHashMap<>();
    int typeId;

    public static SkinType getSkinType(int type) {
        val skinType = SKIN_TYPES.get(type);
        if (skinType != null) {
            return skinType;
        }
        return CUSTOM;
    }

    static {
        for (val skinType : values()) {
            SKIN_TYPES.put(skinType.typeId, skinType);
        }
    }
}
