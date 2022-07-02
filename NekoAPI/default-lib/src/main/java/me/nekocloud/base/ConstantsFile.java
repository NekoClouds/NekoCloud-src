package me.nekocloud.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
public final class ConstantsFile {

    static final Path PATH = Paths.get("/home/nekocl/parlament/constants.yml");
    static final Yaml YAML;
    @NonFinal static Map<String, Object> constants;
    
    public static Map<String, Object> fromYaml(final InputStream is) {
        synchronized (ConstantsFile.YAML) {
            return YAML.load(is);
        }
    }
    
    public static Map<String, Object> fromYaml(final Reader reader) {
        synchronized (ConstantsFile.YAML) {
            return YAML.load(reader);
        }
    }
    
    public static Map<String, Object> fromYaml(final String yaml) {
        synchronized (ConstantsFile.YAML) {
            return YAML.load(yaml);
        }
    }
    
    @Nullable
    public static <T> T get(final String path) {
        if (ConstantsFile.constants == null) {
            return null;
        }
        return (T)ConstantsFile.constants.get(path);
    }
    
    @Contract("_, !null -> !null; _, null -> _")
    @Nullable
    public static <T> T get(final String path, final T defaultValue) {
        return (T) Optional.ofNullable(ConstantsFile.constants)
                .map(constants -> constants.get(path))
                .orElse(defaultValue);
    }
    
    private static void loadConfig() throws IOException {
        if (Files.exists(ConstantsFile.PATH)) {

            try (final InputStream is = Files.newInputStream(ConstantsFile.PATH)) {
                constants = fromYaml(is);
            }

        } else {
            final InputStream resource = ConstantsFile.class.getClassLoader().getResourceAsStream("constants.yml");
            if (resource == null) {
                constants = new HashMap<>();
                System.out.println("Не найден никакой файл настроек!");
                return;
            }

            System.out.println("Не найден файл настроек! Будем использовать стандартные!");
            try (final InputStream is2 = resource) {
                constants = fromYaml(is2);
            }
        }
    }
    
    static {
        final Constructor constructor = new Constructor();
        final PropertyUtils propertyUtils = new PropertyUtils();

        propertyUtils.setSkipMissingProperties(true);
        constructor.setPropertyUtils(propertyUtils);
        YAML = new Yaml(constructor);

        try {
            loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}