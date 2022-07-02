package me.nekocloud.nekoapi.loader;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.game.SubType;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.PlayerInfoLoader;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.nekoapi.commands.*;
import me.nekocloud.nekoapi.donatemenu.DonateMenuListener;
import me.nekocloud.nekoapi.guis.GuiDefaultContainer;
import me.nekocloud.nekoapi.listeners.*;
import me.nekocloud.nekoapi.stats.playtime.PlayTimeManager;
import me.nekocloud.nekoapi.utils.WorldTime;
import me.nekocloud.nekoapi.utils.bukkit.EmptyWorldGenerator;
import me.nekocloud.packetlib.libraries.inventory.def.DefaultGuiThread;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.packetreader.PacketReaderListener;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public final class NekoAPI extends JavaPlugin {

    @Getter
    static NekoAPI instance;

    String username;
    EmptyWorldGenerator generator;

    GuiDefaultContainer guiDefaultContainer;
    DonateMenuListener donateMenuListener;

    PlayTimeManager playTimeManager;
    SoundsListener soundsListener;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        username = CoreConnector.getInstance().getServerName();

        generator = new EmptyWorldGenerator();

        NmsAPI.init(this);
        PublicApiLoader.init(this);

        registerType();

        new GamerListener(this);
        new NetworkingListener(this);
        new ChatListener(this);

        playTimeManager = new PlayTimeManager(this);

        new BungeeMessageListener(this); //синхронизация данных с прокси на которой игрок
        new PacketReaderListener(this); //для голограмм и кликам по ним, как и нпс
        soundsListener = new SoundsListener(this);
//        new FigureFixListener(this); //фикс говна всякого которое в ядре...

        donateMenuListener = new DonateMenuListener(this);
        guiDefaultContainer = new GuiDefaultContainer();

        registerCommands();

        Bukkit.getScheduler().runTaskTimer(this, new WorldTime.TimeTask(), 20L, 30L);
        WorldTime.freezeTime("lobby", 3000L, false);

        new DefaultGuiThread(); // для обновления дефолт гуишек из апи
    }

    @Override
    public void onDisable() {
        closeMysql();
        playTimeManager.disable();
    }

    void closeMysql() {
        val globalBase = GlobalLoader.getMysqlDatabase();
        if (globalBase != null) {
            globalBase.close();
        }

        val playerInfoBase = PlayerInfoLoader.getMysqlDatabase();
        if (playerInfoBase != null) {
            playerInfoBase.close();
        }
    }

    void registerCommands() {
        new ManagerCommand();
        new RulesCommand();
        new MemoryCommand();
        new CrashCommand();
        new LevelCommand();
        new ListCommand();

        switch (GameType.current) {
            case GRIEF, ANARCHY, SB, SURVIVAL -> {
                new GameBloodListener(this);
                // nothing(
            }
            default -> {
                new MoneyCommand();
                new FwCommand();
                new GiveMoneyCommand();
            }
        }

        if (SubType.current != SubType.MISC) {
            soundsListener.setEnableClick(true);
        }

        new GiveKeyCommand();
        new PlayMusicCommand();
        new CloudCrashCommand();
    }

    private void registerType() {
        Spigot spigot = NekoCloud.getGamerManager().getSpigot();
        String serverType = username.split("-")[0];

        try {
            SubType.current = SubType.valueOf(serverType.toUpperCase());
            GameType.current = SubType.current.getGameType();
        } catch (IllegalArgumentException exception) {
            SubType.current = SubType.getByName(System.getProperty("subType", "misc"));
            GameType.current = Arrays.stream(GameType.values())
                    .filter(gameType -> gameType.getLobbyChannel().equalsIgnoreCase(serverType))
                    .findFirst()
                    .orElse(SubType.current.getGameType());
        }

        spigot.sendMessage("Тип сервера определен как §d" + GameType.current.name());
        spigot.sendMessage("Подтип сервера определен как §d" + SubType.current.name());
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return generator;
    }

}