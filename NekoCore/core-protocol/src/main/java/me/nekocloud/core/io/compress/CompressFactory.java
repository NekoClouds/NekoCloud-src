package me.nekocloud.core.io.compress;

import me.nekocloud.core.io.compress.zlib.CoreZlib;
import me.nekocloud.core.io.compress.zlib.JavaZlib;
import me.nekocloud.core.io.compress.zlib.NativeZlib;

public class CompressFactory {

    public static final NativeCode<CoreZlib> ZLIB = new NativeCode<>("zlib-native-compress", JavaZlib::new, NativeZlib::new);

    private CompressFactory() {
        throw new AssertionError();
    }
}
