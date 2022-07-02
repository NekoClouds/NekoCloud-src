package me.nekocloud.base.util.map.cache;

import me.nekocloud.base.util.map.MultikeyMap;

public interface MultikeyCacheMap<I> extends MultikeyMap<I> {

    void cleanUp();
}
