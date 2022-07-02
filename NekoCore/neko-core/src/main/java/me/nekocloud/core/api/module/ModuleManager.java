package me.nekocloud.core.api.module;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.util.GsonUtil;
import me.nekocloud.core.NekoCore;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarFile;

@Log4j2
public final class ModuleManager {

    @Getter
    private final Map<String, CoreModule> moduleMap = new LinkedHashMap<>();

    private final PathMatcher jarPathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.jar");

    @SneakyThrows
    public void loadModules(final @NotNull File modulesFolder) {
        val moduleFileArray = modulesFolder.listFiles();

        if (moduleFileArray == null) {
            return;
        }

        for (val moduleFile : moduleFileArray) {
            loadModuleFile(moduleFile);
        }
    }

    @SneakyThrows
    public void loadModuleFile(final @NotNull File moduleFile) {
        synchronized (moduleMap) {
            if (!moduleFile.getName().endsWith(".jar"))
                return;

            val moduleJar = new JarFile(moduleFile);
            val urlClassLoader = new URLClassLoader(
                    new URL[]{moduleFile.toURI().toURL()}, this.getClass().getClassLoader()
            );

            val moduleJarEntry = moduleJar.getJarEntry("core-module.json");
            val inputStream = moduleJar.getInputStream(moduleJarEntry);
            val moduleInfo = GsonUtil.fromJson(new InputStreamReader(inputStream), CoreModuleInfo.class);

            val clazz = moduleInfo.getMain();
            val moduleMainClass = Class.forName(clazz, true, urlClassLoader);

            val coreModule = ((CoreModule) moduleMainClass.newInstance());

            final FileVisitor<Path> dependenciesFinder = new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (jarPathMatcher.matches(file)) {
                        if (!moduleMap.containsKey(moduleInfo.getName())) {
                            moduleMap.put(moduleInfo.getName(), coreModule);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            };

            coreModule.initialize(moduleFile, moduleJar, moduleInfo);
            coreModule.enableModule();
        }
    }

    public void handleAllModules(final @NotNull CoreModuleHandler coreModuleHandler) {
        for (@NotNull val coreModule : moduleMap.values())
            coreModuleHandler.handle(coreModule);
    }

    public void handleModule(final @NotNull String moduleName, @NotNull CoreModuleHandler coreModuleHandler) {
        val coreModule = getModule(moduleName);
        coreModuleHandler.handle(coreModule);
    }

    public CoreModule getModule(final @NotNull String moduleName) {
        return moduleMap.get(moduleName.toLowerCase());
    }

}
