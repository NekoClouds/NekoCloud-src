package me.nekocloud.core.io.info;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;

public interface InfoField<T> {

    T read(@NonNull ByteBuf buf);

    void write(@NonNull T value, @NonNull ByteBuf buf);
}
