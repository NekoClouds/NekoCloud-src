package me.nekocloud.myserver.type;

import lombok.Getter;
import lombok.SneakyThrows;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.scheduler.CommonScheduler;
import me.nekocloud.core.api.schedulerT.CommonScheduler;
import me.nekocloud.core.api.utility.Directories;
import me.nekocloud.core.api.utility.FileUtil;
import me.nekocloud.core.api.utility.NumberUtil;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.connection.server.impl.BukkitServer;
import me.nekocloud.myserver.NekoMyServer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class PlayerMyServer {

    private final String serverName;
    private final InetSocketAddress inetSocketAddress;

    private final CorePlayer owner;
    private final Collection<CorePlayer> moderatorCollection = new ArrayList<>();

    private final MyServerType serverType;
    private long startMillis;

    private Path serverFolder;
    private Process process;


    private final ExecutorService serverThread = Executors.newCachedThreadPool();

    public PlayerMyServer(@NotNull CorePlayer owner, @NotNull MyServerType myServerType) {
        this.serverName = myServerType.createServerName();
        this.inetSocketAddress = new InetSocketAddress(NumberUtil.randomInt(30_000, 40_000));

        this.owner = owner;
        this.serverType = myServerType;
    }

    private static final String SHELL_SCRIPT = ("java -server -Xmx256M -Dfile.encoding=UTF-8 -jar paper.jar");
    private static final String BATCH_SCRIPT = ("@ECHO OFF \ntitle %server_name% \n\n" + SHELL_SCRIPT);


    public void addModer(@NotNull CorePlayer player) {
        moderatorCollection.add(player);

        MyServerManager.INSTANCE.getPlayerMyServers().put(player.getName().toLowerCase(), this);
        player.sendMessage("§6§lMyServer §8:: §fВы были добавлены в список модераторов сервера " + owner.getDisplayName());
    }

    public void removeModer(@NotNull CorePlayer player) {
        moderatorCollection.remove(player);

        MyServerManager.INSTANCE.getPlayerMyServers().remove(player.getName().toLowerCase());
        player.sendMessage("§6§lMyServer §8:: §fВы были удалены из списка модераторов сервера " + owner.getDisplayName());
    }

    public boolean isModer(@NotNull CorePlayer player) {
        return moderatorCollection.contains(player);
    }

    public boolean isOnlineHere(@NotNull CorePlayer player) {
        return getCoreServer() != null && getCoreServer().getOnlinePlayers().contains(player);
    }

    public boolean isLeader(@NotNull CorePlayer player) {
        return owner.getName().equalsIgnoreCase(player.getName());
    }


    @SuppressWarnings("all")
    @SneakyThrows
    public boolean start() {
        Collection<File> availableServersFiles = Arrays.stream(serverType.getServersFolder().toFile().listFiles())
                .filter(serverFolder -> !MyServerManager.INSTANCE.isAvailable(serverFolder.toPath()))
                .collect(Collectors.toList());

        if (availableServersFiles.isEmpty()) {

            owner.sendMessage("§cВ категории серверов " + serverType.name().toLowerCase(Locale.ROOT) + " нет доступных арен :(");
            owner.sendMessage("§cПопробуйте использовать другую категорию или подождите, пока одна из арен освободится!");
            return false;
        }

        serverThread.submit(() -> {
            owner.sendMessage("§6§lMyServer §8:: §fВаш сервер был инициализирован с названием §a" + serverName);
            owner.sendMessage(" §eЧерез некоторое время Вы будете автоматически перемещены на него!");

            Path serverRunning = NekoMyServer.getInstance().getModuleFolder().toPath().resolve("RunningServers").resolve(serverName);
            Path serverShape = availableServersFiles.stream()
                    .skip((long) (availableServersFiles.size() * Math.random()))
                    .findFirst()
                    .orElse(null).toPath();

            Directories.copyDirectory(serverShape, serverFolder = serverRunning);

            // Create a server process...
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            String script = (isWindows ? BATCH_SCRIPT : SHELL_SCRIPT).replace("%server_name%", getServerName());

            try {

                // Change CoreConnector settings.
                if (Files.exists(serverFolder.resolve("plugins").resolve("CoreConnector"))) {
                    File serverConnectionProperty = serverFolder.resolve("plugins").resolve("CoreConnector").resolve("config.properties").toFile();

                    Properties properties = new Properties();
                    properties.load( new FileReader(serverConnectionProperty) );

                    properties.setProperty("server.name", serverName);
                    properties.store( new FileOutputStream(serverConnectionProperty), null );
                }

                // Change server settings
                if (Files.exists(serverFolder.resolve("server.properties"))) {
                    File serverProperty = serverFolder.resolve("server.properties").toFile();

                    Properties properties = new Properties();
                    properties.load( new FileReader(serverProperty) );


                    properties.setProperty("server-ip", "127.0.0.1");
                    properties.setProperty("server-port", String.valueOf(inetSocketAddress.getPort()));

                    properties.store( new FileOutputStream(serverProperty), null );
                }


                // create a file
                Path bashFile = serverFolder.resolve(isWindows ? "start.bat" : "start.sh");

                if (!Files.exists(bashFile)) {
                    Files.createFile(bashFile);
                }

                // build batch commands
                FileUtil.write(bashFile.toFile(), fileWriter -> fileWriter.write(script));

                // start the process
                ProcessBuilder processBuilder = new ProcessBuilder();

                if (isWindows) {
                    processBuilder.command("cmd.exe", "/c", "start", "start.bat");

                } else {

                    processBuilder.command("sh", "start.sh");
                }

                processBuilder.directory(serverFolder.toFile());
                process = processBuilder.start();
            }

            catch (Exception exception) {
                exception.printStackTrace();
            }

            new CommonScheduler(serverName + "-startprocess") {

                @Override
                @SneakyThrows
                public void run() {

                    if (isRunning()) {
                        cancel();

                        startMillis = System.currentTimeMillis();

                        owner.sendMessage("§6§lMyServer §8:: §fСервер §e" + serverName + " §fуспешно создан, идет подключение...");
                        owner.connectToServer(getCoreServer());

                        process.waitFor();
                        shutdown();
                    }
                }

            }.runTimer(10, 1, TimeUnit.SECONDS);
        });
        return true;
    }

    @SneakyThrows
    public void shutdown() {
        if (getCoreServer() != null) getCoreServer().restart();

        // Delete and destroy server process
        MyServerManager.INSTANCE.removeServer(owner);

        // Destroy process.
        if (process != null) {
            process.destroy();
            process = null;
        }

        // Print messages.
        owner.sendMessage("§6§lMyServer §8:: §fВаш сервер §e" + serverName + " §fбыл выключен и удален");

        for (CorePlayer player : moderatorCollection)
            player.sendMessage("§6§lMyServer §8:: §fСервер §e" + serverName + " §fбыл выключен и удален");

        // Shutdown the server in core.
        serverThread.shutdown();


        // Delete running server folder
        new CommonScheduler(serverName + "-remove-files") {
            // Не полностью удаляется.
            // Скорее всего это связано с тем, что он удаляется в процессе
            // выключения сервера, от чего и не может удалить некоторые файлы,
            // которые запущены ядром (((

            @Override
            public void run() {
                if (serverFolder != null) {
                    Directories.clearDirectory(serverFolder.toFile(), true);

                    serverFolder = null;
                }
            }

        }.runLater(1, TimeUnit.SECONDS);
    }

    public boolean isRunning() {
        return NekoCore.getInstance().getBukkit(serverName) != null;
    }

    public Bukkit getCoreServer() {
        return NekoCore.getInstance().getBukkit(getServerName());
    }


    public void broadcast(@NotNull String message) {

        for (CorePlayer player : getCoreServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

}
