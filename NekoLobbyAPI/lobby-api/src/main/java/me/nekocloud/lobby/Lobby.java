package me.nekocloud.lobby;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.JoinMessage;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.api.leveling.Leveling;
import me.nekocloud.lobby.api.leveling.type.*;
import me.nekocloud.lobby.bossbar.BossBarListener;
import me.nekocloud.lobby.commands.ReloadConfigCommand;
import me.nekocloud.lobby.config.GameConfig;
import me.nekocloud.lobby.config.LobbyConfig;
import me.nekocloud.lobby.config.SettingConfig;
import me.nekocloud.lobby.config.TopConfig;
import me.nekocloud.lobby.customitems.CustomItemListener;
import me.nekocloud.lobby.profile.JumpListener;
import me.nekocloud.lobby.profile.PlayerListener;
import me.nekocloud.lobby.profile.gui.ProfileGuiListener;
import me.nekocloud.lobby.profile.hider.HiderListener;
import me.nekocloud.nekoapi.commands.MoneyCommand;
import me.nekocloud.nekoapi.listeners.JoinListener;
import me.nekocloud.nekoapi.utils.ArmorStandUtil;
import me.nekocloud.nekoapi.utils.WorldTime;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class Lobby extends JavaPlugin { // TODO Открытие профилей других игроков как на хп

    private final Map<String, LobbyConfig> configs = new HashMap<>();

    @Override
    public void onEnable() {

        val settingConfig = initConfig(SettingConfig.class);
        val gameConfig = initConfig(GameConfig.class);
        val topConfig = initConfig(TopConfig.class);

        levelRewards();

        new ReloadConfigCommand(this);
        new MoneyCommand();

        new LobbyGuardListener(this, settingConfig);
        new JumpListener(this);
        new PlayerListener(this, settingConfig);
        new ProfileGuiListener(this);
        new BossBarListener(this);
        new JoinListener(this);
        new CustomItemListener(this, gameConfig);
        new HiderListener(this);

        ArmorStandUtil.fixArmorStand(); //фикс стендов обычных

        if (NekoCloud.isHub()) {
            WorldTime.freezeTime("lobby", 4000, false);
        } else {
            WorldTime.freezeTime("lobby", 1000, false);
        }
    }

    private void levelRewards() {
        Leveling leveling = LobbyAPI.getLeveling();
        leveling.addReward(1, new MoneyLevelReward(500));
        leveling.addReward(2, new MoneyLevelReward(600));
        leveling.addReward(3, new MoneyLevelReward(700));
        leveling.addReward(4, new MoneyLevelReward(800),
                new KeysLevelReward(KeyType.COSMETICS_KEY,1));
        leveling.addReward(5,
                new MoneyLevelReward(900),
                new KeysLevelReward(KeyType.ITEMS_KEY,1));
        leveling.addReward(6, new MoneyLevelReward(1000));
        leveling.addReward(7, new MoneyLevelReward(1100));
        leveling.addReward(8, new MoneyLevelReward(1200),
                new KeysLevelReward(KeyType.GAME_KEY, 1));
        leveling.addReward(9, new MoneyLevelReward(1300));
        leveling.addReward(10,
                new MoneyLevelReward(1400),
                new VirtsLevelReward(75));
        leveling.addReward(11,
                new MoneyLevelReward(2000),
                new KeysLevelReward(KeyType.ITEMS_KEY,2));
        leveling.addReward(12, new MoneyLevelReward(2100));
        leveling.addReward(13, new MoneyLevelReward(2250));
        leveling.addReward(14, new MoneyLevelReward(2300));
        leveling.addReward(15,
                new MoneyLevelReward(2350),
                new KeysLevelReward(KeyType.VIRTS_KEY,1));
        leveling.addReward(16, new MoneyLevelReward(2400));
        leveling.addReward(17, new MoneyLevelReward(2500));
        leveling.addReward(18,
                new MoneyLevelReward(2550),
                new JoinMsgLevelReward(JoinMessage.getMessage(2)));
        leveling.addReward(19, new MoneyLevelReward(2600));

        leveling.addReward(20, new VirtsLevelReward(40),
                new KeysLevelReward(KeyType.COSMETICS_KEY,1),
                new KeysLevelReward(KeyType.ITEMS_KEY,5));

        //=====================================================//
        leveling.addReward(21, new MoneyLevelReward(500));
        leveling.addReward(22, new MoneyLevelReward(600));
        leveling.addReward(23, new MoneyLevelReward(700));
        leveling.addReward(24, new MoneyLevelReward(800));
        leveling.addReward(25,
                new MoneyLevelReward(900),
                new KeysLevelReward(KeyType.ITEMS_KEY,1));

        leveling.addReward(26, new MoneyLevelReward(1000));
        leveling.addReward(27, new MoneyLevelReward(1100));
        leveling.addReward(28, new MoneyLevelReward(1200));
        leveling.addReward(29, new MoneyLevelReward(1300));
        leveling.addReward(30,
                new MoneyLevelReward(1400),
                new VirtsLevelReward(75),
                new KeysLevelReward(KeyType.ITEMS_KEY,2));

        leveling.addReward(31, new MoneyLevelReward(2000));
        leveling.addReward(32, new MoneyLevelReward(2100));
        leveling.addReward(33, new MoneyLevelReward(2250));
        leveling.addReward(34, new MoneyLevelReward(2300));
        leveling.addReward(35,
                new MoneyLevelReward(2350),
                new KeysLevelReward(KeyType.VIRTS_KEY,1));

        leveling.addReward(36, new MoneyLevelReward(2400));
        leveling.addReward(37, new MoneyLevelReward(2500));
        leveling.addReward(38, new MoneyLevelReward(2550));
        leveling.addReward(39, new MoneyLevelReward(2600));

        leveling.addReward(40, new VirtsLevelReward(40),
                new KeysLevelReward(KeyType.COSMETICS_KEY,1),
                new KeysLevelReward(KeyType.ITEMS_KEY,5));

        //=====================================================//
        leveling.addReward(41, new MoneyLevelReward(500));
        leveling.addReward(42, new MoneyLevelReward(600));
        leveling.addReward(43, new MoneyLevelReward(700));
        leveling.addReward(44, new MoneyLevelReward(800));
        leveling.addReward(45,
                new MoneyLevelReward(900),
                new KeysLevelReward(KeyType.ITEMS_KEY,1));

        leveling.addReward(46, new MoneyLevelReward(1000));
        leveling.addReward(47, new MoneyLevelReward(1100));
        leveling.addReward(48, new MoneyLevelReward(1200));
        leveling.addReward(49, new MoneyLevelReward(1300));
        leveling.addReward(50,
                new MoneyLevelReward(1400),
                new VirtsLevelReward(75),
                new KeysLevelReward(KeyType.ITEMS_KEY,2));

        leveling.addReward(51, new MoneyLevelReward(2000));
        leveling.addReward(52, new MoneyLevelReward(2100));
        leveling.addReward(53, new MoneyLevelReward(2250));
        leveling.addReward(54, new MoneyLevelReward(2300));
        leveling.addReward(55,
                new MoneyLevelReward(2350),
                new KeysLevelReward(KeyType.VIRTS_KEY,1));

        leveling.addReward(56, new MoneyLevelReward(2400));
        leveling.addReward(57, new MoneyLevelReward(2500));
        leveling.addReward(58, new MoneyLevelReward(2550));
        leveling.addReward(59, new MoneyLevelReward(2600));

        //=====================================================//
        leveling.addReward(60, new GroupLevelReward(Group.AKIO));
        leveling.addReward(100, new GroupLevelReward(Group.TRIVAL));

    }

    private <T extends LobbyConfig> T initConfig(Class<T> configClass) {
        val name = configClass.getSimpleName().toLowerCase();
        T config = null;
        try {
            config = configClass.getConstructor(Lobby.class).newInstance(this);
            config.load();
            config.init();
            configs.put(name, config);
        } catch (Exception ignored) {
        }
        return config;
    }

    public <T extends LobbyConfig> T getConfig(Class<T> configClass) {
        val name = configClass.getSimpleName().toLowerCase();
        return (T) configs.get(name);
    }

    @Override
    public void reloadConfig() {
        configs.values().forEach(LobbyConfig::load);
    }
}
