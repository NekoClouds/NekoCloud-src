package me.nekocloud.base.skin.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SkinProperty {
    String name;
    String value;
    String signature;

    public Skin toSkin(String skinName, SkinType skinType) {
        return new Skin(skinName, value, signature, skinType, System.currentTimeMillis());
    }
}
