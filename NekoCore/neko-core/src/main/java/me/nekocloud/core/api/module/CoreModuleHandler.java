package me.nekocloud.core.api.module;

import org.jetbrains.annotations.NotNull;

public interface CoreModuleHandler {

    void handle(final @NotNull CoreModule coreModule);
}
