package me.nekocloud.core;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.util.ResourceLeakDetector;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.game.SubType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.query.SchedulerManager;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandManager;
import me.nekocloud.core.api.command.ICommandManager;
import me.nekocloud.core.api.connection.ConnectionHandler;
import me.nekocloud.core.api.connection.INetworkManager;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.player.IPlayerManager;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.api.connection.server.IServerManager;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.core.api.event.EventBus;
import me.nekocloud.core.api.event.player.DisconnectedEvent;
import me.nekocloud.core.api.event.server.BukkitConnectedEvent;
import me.nekocloud.core.api.event.server.BukkitDisconnectedEvent;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.ModuleManager;
import me.nekocloud.core.api.module.command.ModuleCommand;
import me.nekocloud.core.api.module.command.ModulesCommand;
import me.nekocloud.core.api.scheduler.bungee.ScheduledTask;
import me.nekocloud.core.api.scheduler.bungee.TaskScheduler;
import me.nekocloud.core.api.utils.TimedStatsRecorder;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.commands.CoreCommand;
import me.nekocloud.core.common.commands.ShutdownCommand;
import me.nekocloud.core.common.group.command.GroupCommand;
import me.nekocloud.core.common.group.listener.GroupListener;
import me.nekocloud.core.connection.player.PlayerManager;
import me.nekocloud.core.connection.server.ServerManager;
import me.nekocloud.core.netty.NettyHelper;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Log4j2
@Getter @Setter
@NoArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NekoCore implements ConnectionHandler {

    @Getter
    static NekoCore instance                           = new NekoCore();

    // Ебать я гений просто, какой я ахуенный
    Logger logger                                      = log;

    SchedulerManager schedulerManager                  = new SchedulerManager();

    EventBus eventManager                              = new EventBus();
    ICommandManager commandManager                     = new CommandManager();
    IPlayerManager playerManager                       = new PlayerManager();
    IServerManager serverManager                       = new ServerManager();
    INetworkManager networkManager                     = new NetworkManager();

    ModuleManager moduleManager                        = new ModuleManager();
    File modulesFolder                                 = new File("modules");
    File coreFolder                                    = new File("");

    ReentrantReadWriteLock lock                        = new ReentrantReadWriteLock();

    EventLoopGroup workerGroup                         = NettyHelper.newEventLoopGroup(0,
            new ThreadFactoryBuilder().setNameFormat("Netty IO Core Worker Thread #%1$d").build());
    ExecutorService executor                           = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("Core Thread Pool #%1$d").build());

    TaskScheduler taskScheduler                        = new TaskScheduler();
    TimedStatsRecorder connectedServers                = new TimedStatsRecorder(400);
    TimedStatsRecorder disconnectedServers             = new TimedStatsRecorder(400);

    @NonFinal Channel channel;
    @NonFinal CoreSql coreSql;

    @NonFinal boolean running;
    @NonFinal boolean stopping;

    @NonFinal long startSessionMillis;

    public final void start() {
        running = true;
        bind();
    }

    @SneakyThrows
    private void bind() {
        if (System.getProperty("io.netty.leakDetectionLevel") == null)
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        coreSql = new CoreSql(this);

        val address = new InetSocketAddress("127.0.0.2", 1337);
        channel = NettyHelper.newServerBootstrap().group(workerGroup).bind(address)
                .addListener(future -> {

                    if (!future.isSuccess()) {
                        log.warn(ChatColor.RED + "[Channel] Could not bind to host " + address);
                        running = false;
                    } else {
                        log.info(ChatColor.GREEN + "[Channel] Listening on " + address);

                        // Регистрируем команды
                        registerCommands();

                        // Запускаем модули
                        activateModules();

                        // Если все ок регистрируем листенеры
                        registerListeners();
                        initCommons();
                    }
                }).awaitUninterruptibly().channel();

        startSessionMillis = System.currentTimeMillis();
    }

    @SuppressWarnings("all")
    public final void shutdown() {
        Preconditions.checkState(!stopping, "Core already stopping!");
        new Thread("Shutdown Thread") {
            @Override
            public void run() {
                stopping = true;

                coreSql.close();
                logger.info("Closing channel: {}", channel);
                channel.close().syncUninterruptibly();
                logger.info("Closing event loops...");
                workerGroup.shutdownGracefully();

                try {
                    workerGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException ignored) {}

                logger.info("Closing executor...");
                executor.shutdown();

                logger.info("Disable modules....");
                moduleManager.handleAllModules(CoreModule::disableModule);

                running = false;
                System.exit(1);
            }
        }.start();
    }

    @SneakyThrows
    private void activateModules() {
        if (!modulesFolder.exists()) {
            Files.createDirectory(modulesFolder.toPath());
        }

        moduleManager.loadModules(modulesFolder);
        log.info(ChatColor.WHITE + "[ModuleManager] {} modules has been activated", moduleManager.getModuleMap().size());
    }

    private void registerCommands() {
        commandManager.registerCommand(new CoreCommand());
        commandManager.registerCommand(new ModuleCommand());
        commandManager.registerCommand(new ShutdownCommand());
        commandManager.registerCommand(new GroupCommand());
        commandManager.registerCommand(new ModulesCommand());
    }

    private void registerListeners() {
        eventManager.register(new GroupListener());
    }

    private void initCommons() {
        new CoreAuth();
    }

    public final long getSessionMillis() {
        return System.currentTimeMillis() - startSessionMillis;
    }

    public ScheduledTask submitCoreTask(final Runnable runnable, long delay, TimeUnit unit) {
        return this.submitCoreTask(runnable, delay, 0L, unit);
    }

    public ScheduledTask submitCoreTask(
            final Runnable runnable,
            long delay, long period,
            TimeUnit unit
    ) {
        return taskScheduler.schedule(this.executor, ScheduledTask.builder()
                .task(runnable)
                .period(period)
                .delay(delay)
                .scheduler(this.taskScheduler)
                .unit(unit)
                .build());
    }

    public boolean containsPlayer(final String name) {
        return playerManager.getCorePlayerMap()
                .valueCollection().stream().anyMatch(corePlayer ->
                corePlayer.getName().equals(name));
    }

    public int getCachedId(final String name) {
        CorePlayer offlinePlayer = playerManager.getOfflineCache().getIfPresent(name);
        if (offlinePlayer != null) {
            return offlinePlayer.getPlayerID();
        }

        int id = playerManager.getCachedIds().get(name);
        if (id == 0) {
            id = GlobalLoader.getPlayerID(name);
            if (id != -1) {
                playerManager.getCachedIds().put(name, id);
            }
        }
        return id;
    }

    public final @NotNull Collection<Bukkit> getBukkitServers() {
        return serverManager.getBukkitServers().values();
    }

    public final @NotNull Collection<Bungee> getBungeeServers() {
        return serverManager.getBungeeServers().values();
    }

    public final Bukkit getBukkit(final @NotNull String name) {
        return serverManager.getBukkit(name);
    }

    public final Bungee getBungee(final @NotNull String name) {
        return serverManager.getBungee(name);
    }

    public final int getOnline() {
        return getOnlinePlayers().size();
    }

    @Override
    public boolean handleBukkit(
            final @NotNull Bukkit bukkit,
            final @NotNull SocketAddress address
    ) {
        lock.writeLock().lock();
        try {
            if (serverManager.getBukkitServers().containsKey(bukkit.getName())) {
                logger.info("Bukkit already connected with name: {}", bukkit.getName());
                return false;
            }

            connectedServers.record();
            if (!connectedServers.canRecord())
                logger.info("Connected servers [{}]", connectedServers.getRecordCountAndReset());

            serverManager.addBukkit(bukkit);

            callEvent(new BukkitConnectedEvent(bukkit));
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Занести банжу в мапу
     * @param bungee проксти банжа
     * @param address адрес
     * @return true если успешно
     */
    @Override
    public final boolean handleBungee(
            final @NotNull Bungee bungee,
            final @NotNull SocketAddress address
    ) {
        if (serverManager.getBungeeServers().containsKey(bungee.getName())) {
            logger.info("Proxy with name {} is already connected.", bungee.getName());
            return false;
        }

        logger.info("Proxy [{}] connected: {}", bungee.getName(), address);

        serverManager.addBungee(bungee);
        return true;
    }

    /**
     * Занести игрока в мапу
     * @param player игрок
     */
    @Override
    public final void handlePlayer(final @NotNull CorePlayer player) {
        lock.writeLock().lock();
        try {
            if (playerManager.getCorePlayerMap().valueCollection().contains(player)) {
                return;
            }

            playerManager.playerConnect(player);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удалить баккит сервер из мапы
     * @param bukkit баккит сервер
     */
    @Override
    public final void handleBukkitDisconnection(final @NotNull Bukkit bukkit) {
        lock.writeLock().lock();
        try {
            disconnectedServers.record();
            if (!disconnectedServers.canRecord()) {
                logger.info("Disconnected servers [{}] - [{}]",
                        disconnectedServers.getRecordCountAndReset(), bukkit.getName());
            }

            serverManager.removeBukkit(bukkit.getName());

            callEvent(new BukkitDisconnectedEvent(bukkit));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удалить прокси банжу из мапы
     * @param bungee прокси банжа
     */
    @Override
    public final void handleBungeeDisconnection(final @NotNull Bungee bungee) {
        logger.info("Proxy [{}] disconnected: {}", bungee.getName(), bungee.getAddress());

        serverManager.removeBungee(bungee.getName());
    }

    /**
     * Удалить игрока из мапы
     * @param id ид игрока
     */
    @Override
    public final void handlePlayerDisconnection(final int id) {
        val player = playerManager.getPlayer(id);
        if (player == null) {
            logger.info("Disconnecting player not found: " + id);
            return;
        }

        handlePlayerDisconnection(player);
    }

    /**
     * Удалить игрока из мапы
     * @param player игрок
     */
    @Override
    public synchronized final void handlePlayerDisconnection(final @NotNull CorePlayer player) {
        lock.writeLock().lock();

        try {
            playerManager.playerDisconnect(player);

            playerManager.getOfflineCache().cleanUp();
            playerManager.getOfflineCache().put(player.getName().toLowerCase(), player);

            callEvent(new DisconnectedEvent(player));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получить игрока по его номеру
     * @param playerId - номер игрока
     */
    public final CorePlayer getPlayer(final int playerId) {
        return playerManager.getPlayer(playerId);
    }

    /**
     * Получить игрока по его нику
     * @param playerName - ник игрока
     */
    public final CorePlayer getPlayer(final @NotNull String playerName) {
        return playerManager.getPlayer(playerName);
    }

    /**
     * Получить оффлайн данные игрока по его нику
     * @param playerName ник игрока
     */
    public final CorePlayer getOfflinePlayer(final @NotNull String playerName) {
        return playerManager.getOfflinePlayer(playerName);
    }

    public final CorePlayer getOfflinePlayer(int playerID) {
        return playerManager.getOfflinePlayer(networkManager.getPlayerName(playerID));
    }

    /**
     * Получить всех подключенных игроков к кору
     *
     * @return игроки
     */
    public final Collection<CorePlayer> getOnlinePlayers() {
        return playerManager.getOnlinePlayers(player -> true);
    }

    /**
     * Получить отфлитрованный список онлайн игроков
     * по какому-то условию
     *
     * @param responseHandler условие фильтрования онлайн игроков
     */
    public final Collection<CorePlayer> getOnlinePlayers(final @NotNull PlayerManager.PlayerResponseHandler responseHandler) {
        return playerManager.getOnlinePlayers(responseHandler);
    }

    public final Bungee getBestBungee() {
        return getBungeeServers()
                .stream()
                .min(Comparator.comparingInt(Bungee::getOnline))
                .orElse(null);
    }

    /**
     * Получить сумму онлайна нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public final int getOnlineByServerPrefix(final @NotNull String serverPrefix) {
        return serverManager.getOnlineByServerPrefix(serverPrefix);
    }

    public final int getConnectedServersCount(final @NotNull String serverPrefix) {
        return (int) (getBukkitServers().stream()
                .filter(s -> s.getName().toLowerCase(Locale.ROOT).startsWith(serverPrefix.toLowerCase()))
                .count());
    }

    public final Collection<Bukkit> getConnectedServers(final @NotNull String serverPrefix) {
        return getBukkitServers().stream()
                .filter(s -> s.getName().toLowerCase(Locale.ROOT).startsWith(serverPrefix.toLowerCase()))
                .collect(Collectors.toSet());
    }

    public Bukkit getBestByType(final boolean checkOnline, final @NotNull GameType gameType) {
        Collection<Bukkit> connectedServers = getConnectedServers(gameType.getName());
        if (connectedServers.isEmpty())
            return null;

        if (checkOnline) {
            return connectedServers.stream().max(Comparator.comparing(Bukkit::getOnline))
                    .orElse(null);
        } else {
            return connectedServers.stream().findFirst().orElse(null);
        }
    }

    public Bukkit getBestByType(final @NotNull GameType serverSubMode) {
        return getBestByType(true, serverSubMode);
    }

    public Bukkit getBestSubType(final @NotNull SubType subType) {
        return getBestByType(subType.getGameType());
    }


    /**
     * Получить список нескольких серверов
     * по указанному префиксу
     *
     * @param serverPrefix префикс серверов
     */
    public final Collection<Bukkit> getServersByPrefix(final @NotNull String serverPrefix) {
        return serverManager.getServersByPrefix(serverPrefix);
    }

    /**
     * Получить список нескольких прокси серверов
     * по указанному префиксу
     *
     * @param serverPrefix - префикс серверов
     */
    public final Collection<Bungee> getProxiesByPrefix(final @NotNull String serverPrefix) {
        return serverManager.getProxiesByPrefix(serverPrefix);
    }

    @Contract("_ -> param1")
    public final <T extends Event> @NotNull T callEvent(T event) {
        Preconditions.checkNotNull(event, "event is null");
        eventManager.post(event);
        event.postCall();

        return event;
    }
}
