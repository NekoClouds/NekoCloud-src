package me.nekocloud.base.gamer.constans;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum KeyType {
    //0 - делать нельзя, тк 0 код опыта!!!!!
    ITEMS_KEY(1, 20, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc4YWJkZjk4MmY5MDllYWI1ZGU5YmY5NjljZjE0ZjY2NGRiNGM0NDc3Mzg0NTllYTQwMTYyYjM3ZDEyNCJ9fX0"),
    COSMETICS_KEY(2, 22, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc4YWJkZjk4MmY5MDllYWI1ZGU5YmY5NjljZjE0ZjY2NGRiNGM0NDc3Mzg0NTllYTQwMTYyYjM3ZDEyNCJ9fX0="),
    GROUP_KEY(3, 24, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ5ODg2YTU3YjVlNWIxMzZkZGIyYTk0NzkxZjliN2U3MzE2ZTVkODY0NmYxN2EwNDdkNWY5NWE3NDg0Y2IifX19"),
    VIRTS_KEY(4, 30, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc4YWJkZjk4MmY5MDllYWI1ZGU5YmY5NjljZjE0ZjY2NGRiNGM0NDc3Mzg0NTllYTQwMTYyYjM3ZDEyNCJ9fX0="),
    GAME_KEY(5, 32, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ5ODg2YTU3YjVlNWIxMzZkZGIyYTk0NzkxZjliN2U3MzE2ZTVkODY0NmYxN2EwNDdkNWY5NWE3NDg0Y2IifX19"),
    ;

    static final Int2ObjectMap<KeyType> KEY_TYPES = new Int2ObjectOpenHashMap<>();

    int id, slotGui; //слот гуи
    String headValue; //голова

    public @NotNull String getName(@NotNull Language language) {
        return language.getMessage(this.name());
    }

    public @NotNull List<String> getLore(@NotNull Language language) {
        return language.getList(this.name() + "_LORE");
    }

    @Nullable
    public static KeyType getKey(int id) {
        return KEY_TYPES.get(id);
    }

    static {
        for (val keyType : values()) {
            KEY_TYPES.put(keyType.id, keyType);
        }
    }
}
