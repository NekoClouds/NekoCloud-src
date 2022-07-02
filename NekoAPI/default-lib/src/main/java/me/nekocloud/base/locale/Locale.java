package me.nekocloud.base.locale.deprecated;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
class Locale {

    private static final Yaml YAML = newSnakeYaml();

    private static Yaml newSnakeYaml() {
        Constructor constructor = new Constructor();
        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        constructor.setPropertyUtils(propertyUtils);

        return new Yaml(constructor);
    }

    private final String name;
    private int size;
    private final Map<String, String> messages = new ConcurrentHashMap<>();
    private final Map<String, List<String>> listMessages = new ConcurrentHashMap<>();

    Locale(String name) {
        this.name = name;
        this.size = 0;

        try {
            loadFromSite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadFromSite() throws IOException {
        messages.clear();
        listMessages.clear();
        URL oracle = new URL("https://raw.githubusercontent.com/NekoClouds/locale/master/src/lang/" + name + ".yml");
        Map<Object, Object> map = YAML.loadAs(new InputStreamReader(oracle.openStream()), LinkedHashMap.class);
        if (map == null)
            return;

        this.size = map.size();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object data = entry.getValue();

            if (data instanceof List) {
                listMessages.put(key, (List<String>) data);
                continue;
            }

            messages.put(key, data.toString());
        }
    }
}
