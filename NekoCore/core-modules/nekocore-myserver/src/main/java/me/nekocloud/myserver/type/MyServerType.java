package me.nekocloud.myserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.NekoCore;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Getter
public enum MyServerType {

    SWSN(MyServerCategory.SKYWARS, "Solo Normal"),
    SWSI(MyServerCategory.SKYWARS, "Solo Insane"),
    SWDN(MyServerCategory.SKYWARS, "Double Normal"),
    SWDI(MyServerCategory.SKYWARS, "Double Insane"),

    LWSN(MyServerCategory.LUCKYWARS, "Solo Normal"),
    LWSI(MyServerCategory.LUCKYWARS, "Solo Insane"),
    LWDN(MyServerCategory.LUCKYWARS, "Double Normal"),
    LWDI(MyServerCategory.LUCKYWARS, "Double Insane"),
    ;


    public static final MyServerType[] SERVER_TYPES = values();

    public static MyServerType of(int typeIndex) {
        for (MyServerType myServerType : SERVER_TYPES)

            if (myServerType.getSubTypeIndex() == typeIndex)
                return myServerType;

        return null;
    }

    public static MyServerType of(String serverPrefix) {
        for (MyServerType myServerType : SERVER_TYPES)

            if (myServerType.name().equalsIgnoreCase(serverPrefix))
                return myServerType;

        return null;
    }


    private final MyServerCategory category;
    private final String subName;

    public int getSubTypeIndex() {
        return ordinal() + category.getTypeIndex();
    }

    public String createServerName() {
        return category.createServerName();
    }

    public Path getServersFolder() {
        return NekoCore.getInstance().getModulesFolder().toPath().resolve("NekoMyServer")
                .resolve(Paths.get(category.getServersPath() + File.separator + subName.split(" ")[1] + File.separator + subName.split(" ")[0]));
    }

}
