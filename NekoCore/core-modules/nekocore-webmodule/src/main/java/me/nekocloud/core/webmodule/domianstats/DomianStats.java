package me.nekocloud.core.webmodule.domianstats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.config.Configuration;
import me.nekocloud.core.api.config.ConfigurationProvider;
import me.nekocloud.core.api.config.JsonConfiguration;
import me.nekocloud.core.webmodule.Webmodule;

import java.io.File;
import java.nio.file.Files;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class DomianStats {

    Configuration config;
    NekoCore nekoCore;
    Webmodule webmodule;

    public DomianStats(NekoCore nekoCore, Webmodule webmodule) {
        this.config = loadConfig();
        this.nekoCore = nekoCore;
        this.webmodule = webmodule;

        getWebmodule().getManagement().registerListener(new DomianListener());
    }

    private Configuration loadConfig() {
        webmodule.saveResource("dstats.json");
        val config = new File(getWebmodule().getModuleFolder(),"dstats.json");
        Configuration cfg = null;
        try {
            if (!config.exists()) {
                Files.copy(getWebmodule().getResourceAsStream("dstats.json"), config.toPath());
            }
            cfg = ConfigurationProvider.getProvider(JsonConfiguration.class).load(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cfg;
    }
}
