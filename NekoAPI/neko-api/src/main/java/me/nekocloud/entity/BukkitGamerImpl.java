package me.nekocloud.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.ActionBarAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.TitleAPI;
import me.nekocloud.api.event.gamer.*;
import me.nekocloud.api.event.gamer.async.AsyncGamerLoadSectionEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.GamerBase;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.*;
import me.nekocloud.base.gamer.friends.Friend;
import me.nekocloud.base.gamer.friends.FriendAction;
import me.nekocloud.base.gamer.sections.*;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.connector.CoreConnector;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.core.io.packet.bukkit.BukkitGroupPacket;
import me.nekocloud.core.io.packet.bukkit.BukkitNetworking;
import me.nekocloud.core.io.packet.bukkit.BukkitSetting;
import me.nekocloud.nekoapi.utils.ProtocolSupportUtil;
import me.nekocloud.nekoapi.utils.ViaUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
public final class BukkitGamerImpl extends GamerBase implements BukkitGamer {

    static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    static final ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();
    static final ActionBarAPI ACTION_BAR_API = NekoCloud.getActionBarAPI();
    static final TitleAPI TITLE_API = NekoCloud.getTitlesAPI();
    static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    static final boolean PROTOCOL_SUPPORT = Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null;

    @Getter
    final InetAddress ip;

    @Setter
    Player player;
    ItemStack head;
    Version version;

    public BukkitGamerImpl(AsyncPlayerPreLoginEvent event) {
        super(event.getName());

        this.ip = event.getAddress();

        SkinSection section = getSection(SkinSection.class);
        if (section != null) {
            this.head = Head.getHeadByValue(section.getSkin().getValue());
        } else {
            this.head = ItemUtil.getBuilder(Material.SKULL_ITEM)
                    .setDurability((short) 3)
                    .build();
        }
    }

    @Override
    protected Set<Class<? extends Section>> initSections() {
        AsyncGamerLoadSectionEvent event = new AsyncGamerLoadSectionEvent(this);
        BukkitUtil.callEvent(event);
        return event.getSections();
    }

    @Override
    public CommandSender getCommandSender() {
        return this.getPlayer();
    }

    @Override
    public void sendMessage(String message) {
        if (player == null || !player.isOnline()) {
            return;
        }

        player.sendMessage(message);
    }

    @Override
    public Version getVersion() {
        if (version != null) {
            return version;
        }


        if (player == null || !player.isOnline()) {
            version = Version.EMPTY;
        } else {
            try {
                if (PROTOCOL_SUPPORT) {
                    version = ProtocolSupportUtil.getVersion(player);
                } else {
                    version = ViaUtil.getVersion(player);
                }
            } catch (Exception e) {
                version = Version.EMPTY;
            }
        }

        return version;
    }

    @Override
    public Player getPlayer() {
        if (player == null) {
            return (player = Bukkit.getPlayer(this.name));
        }

        return player;
    }

    @Override
    public ItemStack getHead() {
        if (head == null) {
            return (this.head = ItemUtil.getBuilder(Material.SKULL_ITEM)
                    .setDurability((short) 3)
                    .build()).clone();
        }
        return this.head.clone();
    }

    @Override
    public String getChatName() {
        return getDisplayName();
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public void setLanguage(Language language) {
        if (player == null) {
            return;
        }

        Language oldLanguage = this.getLanguage();
        if (oldLanguage == language) {
            return;
        }

        // Отправляем пакет на кору для обновления
        sendPacket(new BukkitSetting.Lang(getPlayerID(), language, oldLanguage));

        GamerChangeLanguageEvent event = new GamerChangeLanguageEvent(this, language, oldLanguage);
        BukkitUtil.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        getSection(SettingsSection.class).setLang(language, true);
    }

    @Override
    public ChatColor getPrefixColor() {
        try {
             return ChatColor.getByChar(getPrefix().charAt(1));
        } catch (Exception ignored) {}

        return ChatColor.GRAY;
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        if (player == null) {
            return;
        }

        player.spigot().sendMessage(baseComponents);
    }

    @Override
    public void setPrefix(String prefix) {
        if (getPrefix().equalsIgnoreCase(prefix))
            return;

        super.setPrefix(prefix);
        if (player == null) {
            return;
        }

        player.setDisplayName("§r" + prefix + player.getName());

        // Отправляем пакет на кору для обновления
        sendPacket(new BukkitSetting.Prefix(getPlayerID(), prefix));

        GamerChangePrefixEvent event = new GamerChangePrefixEvent(this, prefix);
        BukkitUtil.runTask(()-> BukkitUtil.callEvent(event));
    }

    @Override
    public boolean addExp(int exp) {
        final NetworkingSection networkingSection = getSection(NetworkingSection.class);
        boolean result = networkingSection.addExp(exp);

        if (result && player != null) {
            int newLevel = networkingSection.getLevel();

            // Отправляем пакет на кору для обновления
            sendPacket(new BukkitNetworking(getPlayerID(), BukkitNetworking.Type.EXP, exp));

            GamerLvlUpEvent event = new GamerLvlUpEvent(this, newLevel, networkingSection.getExpNextLevel());
            BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));
        }

        return result;
    }

    @Override
    public int getMoney(PurchaseType purchaseType) {
        return getSection(MoneySection.class).getMoney(purchaseType);
    }

    @Override
    public void setMoney(PurchaseType purchaseType, int money) {
        if (money < 0) {
            return;
        }
        MoneySection section = getSection(MoneySection.class);
        section.setMoneyToMysql(purchaseType, money);

        // Отправляем пакет на кору
       sendPacket(new BukkitNetworking(getPlayerID(),
                BukkitNetworking.Type.MONEY,
                purchaseType.getId(),
                money));
    }

    @Override
    public boolean changeMoney(PurchaseType purchaseType, int amount) {
        MoneySection section = getSection(MoneySection.class);
        boolean result = section.changeMoney(purchaseType, amount);

        if (result) {
            // Отправляем пакет на кору для обновления
            sendPacket(new BukkitNetworking(getPlayerID(),
                BukkitNetworking.Type.MONEY,
                purchaseType.getId(),
                amount));
        } else {
            SOUND_API.play(player, SoundType.NO);
            sendMessage("");
            sendMessageLocale("GAMER_NO_" + purchaseType.name());
            sendMessage("");
        }
        return result;
    }

    @Override
    public void changeFriend(FriendAction friendAction, Friend friendID) {
        getSection(FriendsSection.class).changeFriend(friendAction, friendID);
        BukkitUtil.callEvent(new GamerFriendEvent(this,
                new BukkitFriend(friendID.getPlayerId()), friendAction));
    }

    @Override
    public int getKeys(KeyType keyType) {
        return getSection(NetworkingSection.class).getKeys(keyType);
    }

    @Override
    public void setKeys(KeyType keyType, int keys) {
        if (keys < 0 || keyType == null) {
            return;
        }
        NetworkingSection section = getSection(NetworkingSection.class);
        section.setKeysToMysql(keyType, keys);

        sendPacket(new BukkitNetworking(getPlayerID(),
                BukkitNetworking.Type.KEYS,
                keyType.getId(),
                keys));
    }

    @Override
    public boolean changeKeys(KeyType keyType, int amount) {
        if (keyType == null) {
            return false;
        }

        NetworkingSection networkingSection = getSection(NetworkingSection.class);
        boolean result = networkingSection.changeKeys(keyType, amount);
        if (result) {
            // Отправляем пакет на кору для обновления
            sendPacket(new BukkitNetworking(getPlayerID(),
                BukkitNetworking.Type.KEYS,
                keyType.getId(),
                amount));

        } else {
            SOUND_API.play(player, SoundType.NO);
            sendMessage("");
            sendMessageLocale("GAMER_NO_KEYS");
            sendMessage("");
        }
        return result;
    }

    @Override
    public void sendTitle(String title, String subTitle) {
        TITLE_API.sendTitle(getPlayer(), title, subTitle);
    }

    @Override
    public void sendTitle(String title, String subTitle, long fadeInTime, long stayTime, long fadeOutTime) {
        TITLE_API.sendTitle(player, title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    @Override
    public void sendActionBar(String msg) {
        ACTION_BAR_API.sendBar(getPlayer(), msg);
    }

    @Override
    public void sendActionBarLocale(String key, Object... objects) {
        ACTION_BAR_API.sendBar(player, this.getLanguage().getMessage(key, objects));
    }

    @Override
    public int getFriendsLimit() {
        FriendsSection section = getSection(FriendsSection.class);
        return section.getFriendsLimit();
    }

    @Override
    public Int2ObjectMap<Friend> getFriends() {
        FriendsSection section = getSection(FriendsSection.class);
        Int2ObjectMap<Friend> friends = new Int2ObjectOpenHashMap<>();
        for (int friend : section.getFriends()) {
            friends.put(friend, new BukkitFriend(friend));
        }

        return friends;
    }

    @Override
    public double getMultiple() {
        return getSection(MoneySection.class).getMultiple();
    }

    @Override
    public void playSound(Sound sound) {
        SOUND_API.play(player, sound);
    }

    @Override
    public void playSound(Sound sound, float volume, float pitch) {
        SOUND_API.play(player, sound, volume, pitch);
    }

    @Override
    public void playSound(SoundType sound) {
        playSound(SOUND_API.getSound(sound));
    }

    @Override
    public void playSound(SoundType sound, float volume, float pitch) {
        playSound(SOUND_API.getSound(sound), volume, pitch);
    }

    @Override
    public JoinMessage getJoinMessage() {
        return getSection(JoinMessageSection.class).getJoinMessage();
    }

    @Override
    public void setDefaultJoinMessage(JoinMessage joinMessage) {
        BukkitUtil.runTaskAsync(() -> getSection(JoinMessageSection.class).setDefaultJoinMessage(joinMessage, true));
    }

    @Override
    public void addJoinMessage(JoinMessage joinMessage) {
        BukkitUtil.runTaskAsync(() -> getSection(JoinMessageSection.class).addJoinMessage(joinMessage, true));
    }

    @Override
    public List<JoinMessage> getAvailableJoinMessages() {
        return getSection(JoinMessageSection.class).getAvailableJoinMessage();
    }

    @Override
    public void setSetting(SettingsType type, boolean key) {
        if (getSetting(type) == key || player == null) {
            return;
        }

        super.setSetting(type, key);

        // Отправляем пакет на кору для обновления
        sendPacket(new BukkitSetting(getPlayerID(), type, key));

        GamerChangeSettingEvent event = new GamerChangeSettingEvent(this, type, key);
        BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));
    }

    @Override
    public void setGroup(final Group group) {
        if (getGroup() == group || player == null) {
            return;
        }

        super.setGroup(group);

        // Отправляем пакет на кору для обновления
        sendPacket(new BukkitGroupPacket(getPlayerID(), group.getLevel()));

        // Обновляем в борде(хз почему этого не было на хепликсе)
        SCORE_BOARD_API.setPrefix(getPlayer(), group.getPrefix());

        GamerChangeGroupEvent event = new GamerChangeGroupEvent(this, group);
        BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));
    }

    public void setHead(String value) {
        this.head = Head.getHeadByValue(value);
    }

    @Override
    public boolean isFriend(int playerID) {
        return getFriends().containsKey(playerID);
    }

    @Override
    public boolean isFriend(IBaseGamer gamer) {
        if (gamer == null) {
            return false;
        }

        return isFriend(gamer.getPlayerID());
    }

    @Override
    public boolean isFriend(Player player) {
        if (player == null) {
            return false;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return false;
        }

        return isFriend(gamer.getPlayerID());
    }

    @Override
    public boolean isFriend(Friend friend) {
        return isFriend(friend.getPlayerId());
    }

    private void sendPacket(@NonNull DefinedPacket packet) {
        CoreConnector.getInstance().sendPacket(packet);
    }
}
