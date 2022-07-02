package me.nekocloud.core.io.compress;

import io.netty.util.internal.SystemPropertyUtil;
import me.nekocloud.core.io.compress.zlib.CoreZlib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

public final class NativeCode<T> {

    private final String name;

    private final Supplier<T> javaImpl;
    private final Supplier<T> nativeImpl;

    private boolean loaded;

    public NativeCode(String name, Supplier<T> javaImpl, Supplier<T> nativeImpl) {
        this.name = name;
        this.javaImpl = javaImpl;
        this.nativeImpl = nativeImpl;
    }

    public T newInstance() {
        return (loaded) ? nativeImpl.get() : javaImpl.get();
    }

    public boolean isNativeCodeLoaded() {
        return loaded;
    }

    public boolean load() {
        if (!loaded && isSupported()) {
            String fullName = "core-" + name;

            try {
                System.loadLibrary(fullName);
                loaded = true;
            } catch (Throwable t) {
            }

            if (!loaded) {
                try {
                    InputStream soFile = CoreZlib.class.getClassLoader().getResourceAsStream(name + ".so");

                    if (soFile == null) {
                        throw new IOException("Cannot find compiled library in class path.");
                    }

                    Path temp = Files.createTempFile(fullName, ".so");

                    Files.copy(soFile, temp, StandardCopyOption.REPLACE_EXISTING);

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            Files.deleteIfExists(temp);
                        } catch (IOException e) {
                            //похуй...
                        }
                    }));

                    System.load(temp.toAbsolutePath().toString());
                    loaded = true;
                } catch (IOException ex) {
                    //особого значения не имеет
                } catch (UnsatisfiedLinkError ex) {
                    System.out.println("Could not load native library: " + ex.getMessage());
                }
            }
        }

        return loaded;
    }

    public static boolean isSupported() {
        return "Linux".equals(System.getProperty("os.name")) && "amd64".equals(System.getProperty("os.arch")) &&
                SystemPropertyUtil.getBoolean("useNativeCompression", false);
    }
}
