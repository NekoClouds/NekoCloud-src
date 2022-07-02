package me.nekocloud.myserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.api.utility.NumberUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum MyServerCategory {

    SKYWARS(1, "SkyWars", "SWL", ("Servers" + File.separator + "SkyWars")),
    LUCKYWARS(2, "LuckyWars", "LWL", ("Servers" + File.separator + "LuckyWars")),
    ;


    public static final MyServerCategory[] SERVER_CATEGORIES = values();

    public static MyServerCategory of(int typeIndex) {
        for (MyServerCategory category : SERVER_CATEGORIES)

            if (category.getTypeIndex() == typeIndex)
                return category;

        return null;
    }

    public static MyServerCategory of(@NotNull String name) {
        for (MyServerCategory category : SERVER_CATEGORIES)

            if (category.getName().equalsIgnoreCase(name))
                return category;

        return null;
    }


    private final int typeIndex;

    private final String name;
    private final String lobbyPrefix;

    private final String serversPath;


    public String createServerName() {
        String serverIndex = String.valueOf(NumberUtil.randomInt(1, 1000));
        String serverName = ("ms-").concat(name.toLowerCase()).concat("-").concat(serverIndex);

        if (MyServerManager.INSTANCE.hasServer(serverName)) {
            return createServerName();
        }

        return serverName;
    }

    public Collection<MyServerType> getServerTypes() {
        return Arrays.stream(MyServerType.SERVER_TYPES)
                .filter(myServerType -> myServerType.getCategory().equals(this))
                .collect(Collectors.toList());
    }

}
