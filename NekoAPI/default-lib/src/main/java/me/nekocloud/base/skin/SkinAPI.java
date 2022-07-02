package me.nekocloud.base.skin;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.sections.SkinSection;
import me.nekocloud.base.skin.exeptions.SkinRequestException;
import me.nekocloud.base.skin.exeptions.TooManyRequestsSkinException;
import me.nekocloud.base.skin.services.ElySkinService;
import me.nekocloud.base.skin.services.MojangSkinService;
import me.nekocloud.base.skin.services.SkinService;
import me.nekocloud.base.sql.GlobalLoader;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@Getter
public class SkinAPI {

    private final SkinService ELY_API = new ElySkinService("https://account.ely.by/api/mojang/profiles/",
            "http://skinsystem.ely.by/textures/signed/");
    private final SkinService MOJANG_API = new MojangSkinService("https://api.mojang.com/users/profiles/minecraft/",
            "https://sessionserver.mojang.com/session/minecraft/profile/");

    /**
     * ВНИМАНИЕ! Если вызывать часто, то mojang или Елу забанят!
     * получить скин по нику игрока или скина
     * @param skinName - ник игрока или скина
     * @return - скин
     */
    public Skin getSkin(String skinName) {
        // Система эли не нужна нам
//        try {
//            System.out.println("Скин элу бу");
//            return ELY_API.getSkinByName(skinName);
//        } catch (SkinRequestException e) {
//            System.out.println("Скин элу -");
//            if (e instanceof TooManyRequestsSkinException) {
//                System.out.println("Много запросов suka");
//                return GlobalLoader.getSkin(skinName);
//            }
            try {
                return MOJANG_API.getSkinByName(skinName);
            } catch (SkinRequestException e) {
                if (e instanceof TooManyRequestsSkinException) {
                    return GlobalLoader.getSkin(skinName);
                }
                return Skin.DEFAULT_SKIN;
            }
        }
//    }

    public static Skin getSkin(@NotNull IBaseGamer gamer, @NotNull SkinType skinType) {
        SkinSection skinSection = gamer.getSection(SkinSection.class);
        String skinName = skinSection != null ? skinSection.getSkinName() : gamer.getName();
        try {
            switch (skinType) {
                case ELY: {
                    return ELY_API.getSkinByName(skinName);
                }
                case MOJANG: {
                    return MOJANG_API.getSkinByName(skinName);
                }
            }
        } catch (SkinRequestException e) {
            e.printStackTrace();
        }

        return GlobalLoader.getSkin(skinName);
    }

//    public Skin getSkin(IBaseGamer gamer, SkinType skinType) {
//        String skinName = gamer.getName();
//        val skinSection = gamer.getSection(SkinSection.class);
//        if (skinSection != null) {
//            skinName = skinSection.getSkinName();
//        }
//
//        if (skinType == SkinType.ELY) {
//            try {
//                ELY_API.getSkinByName(skinName);
//            } catch (Exception e) {
//                return GlobalLoader.getSkin(skinName);
//            }
//        }
//
//        if (skinType == SkinType.MOJANG) {
//            try {
//                MOJANG_API.getSkinByName(skinName);
//            } catch (Exception e) {
//                return GlobalLoader.getSkin(skinName);
//            }
//        }
//
//        return GlobalLoader.getSkin(skinName);
//    }

    public Skin getSkin(@NotNull IBaseGamer gamer) {  //todo тут грузить скин в зависимости от того, какая настройка стоит у игрока(мол елу или не елу)
        val skinSection = gamer.getSection(SkinSection.class);
        if (skinSection != null) {
            return getSkin(skinSection.getSkinName());
        }

        return getSkin(gamer.getName());
    }
}
