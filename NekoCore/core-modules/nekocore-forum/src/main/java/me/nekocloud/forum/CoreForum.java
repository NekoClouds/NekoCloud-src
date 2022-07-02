package me.nekocloud.forum;

import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;

@CoreModuleInfo(name = "CoreForum", author = "_Novit_", depends = "Webmodule", version = "1.0")
public class CoreForum extends CoreModule {

    @Override
    protected void onEnable() {
        // Модуль сука ебаный напишись сам
        // ТЗ Модуля:
        // Синхронизация данных игроков
        // Синхронизация банлиста
        // Вывод стаффа, онлайна, донатеров и прочего дерьма
        // Объявление стаффу в вк о новом топике
    }

    @Override
    protected void onDisable() {

    }
}
