package me.nekocloud.core.api.module;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.NekoCore;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.jar.JarFile;

@Getter
@Log4j2
public abstract class CoreModule {

    protected JarFile moduleJar;
    protected File moduleFile;

    protected String name;
    protected String[] authors;
    protected String version;
    protected String[] depends;

    protected File moduleFolder;

    protected long enableMillis;

    @Setter
    protected boolean enabled;

    @Setter
    protected CoreModuleInfo moduleInfo;

    protected final NekoCore core                   = NekoCore.getInstance();
    protected final CoreModuleManagement management = new CoreModuleManagement(this);


    protected abstract void onEnable();
    protected abstract void onDisable();


    public void initialize(
            final @NotNull File moduleFile,
            final @NotNull JarFile moduleJar,
            final @NotNull CoreModuleInfo moduleInfo
    ) {
        this.moduleJar = moduleJar;
        this.moduleFile = moduleFile;

        this.name = moduleInfo.getName();
        this.authors = moduleInfo.getAuthors();
        this.version = moduleInfo.getVersion();
        this.depends = moduleInfo.getDepends() == null ? new String[]{"Пусто"} : moduleInfo.getDepends();

        this.moduleFolder = NekoCore.getInstance().getModulesFolder()
                .toPath()
                .resolve(name)
                .toFile();
    }

    public void enableModule() {
        setEnabled(true);
        log.info("[ModuleManager] Enable module " + name + " v" + version + " by " + Arrays.toString(authors));

        NekoCore.getInstance().getModuleManager()
                .getModuleMap().put(getName().toLowerCase(), this);

        this.enableMillis = System.currentTimeMillis();
        this.onEnable();
    }

    public void disableModule() {
        setEnabled(false);
        log.info("[ModuleManager] Disable module " + name + " by " + Arrays.toString(authors));

        management.unregisterCommands();
        management.unregisterListeners();

        this.onDisable();
    }

    public void reloadModule() {
        unloadModule();
        loadModule();
    }


    @SneakyThrows
    public void unloadModule() {
        disableModule();

        moduleJar.close();
    }

    @SneakyThrows
    public void loadModule() {
        NekoCore.getInstance().getModuleManager().loadModuleFile(moduleFile);
    }

    @SneakyThrows
    public void saveResource(final @NotNull String resourceName) {
        val moduleFolderPath = moduleFolder.toPath();
        val resourcePath = moduleFolderPath.resolve(resourceName);

        if (!Files.exists(moduleFolderPath)) {
            Files.createDirectory(moduleFolderPath);
        }

        if (!Files.exists(resourcePath)) {
            Files.copy(getResourceAsStream(resourceName), resourcePath);
        }
    }

    @SneakyThrows
    public final InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream( resourceName );
    }
}
