package me.nekocloud.market;

import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.api.util.ConfigManager;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.utils.core.CoreUtil;
import me.nekocloud.market.auction.AuctionItemImpl;
import me.nekocloud.market.auction.AuctionManager;
import me.nekocloud.market.command.AuctionCommand;
import me.nekocloud.market.command.ConvertCommand;
import me.nekocloud.market.command.ReloadConfigCommand;
import me.nekocloud.market.command.ShopCommand;
import me.nekocloud.market.player.PlayerListener;
import me.nekocloud.market.shop.ShopGui;
import me.nekocloud.market.shop.ShopItem;
import me.nekocloud.market.shop.ShopManager;
import me.nekocloud.market.utils.PlayerLoader;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

public final class Market extends JavaPlugin {

    private static String database;

    private ShopCommand shopCommand;
    private AuctionCommand auctionCommand;

    private ShopManager shopManager;
    @Getter
    private AuctionManager auctionManager;

    private FileConfiguration config;

    @Override
    public void onEnable() {
        File file = new File(CoreUtil.getConfigDirectory() + "/configShop.yml");
        if (!file.exists()) {
            sendMessage("§c[Market] [ERROR] §fКонфиг не найден! Магазин работать не будет! Плагин отключен!");
            return;
        }

        ConfigManager configManager = new ConfigManager(file);
        this.config = configManager.getConfig();

        database = config.getString("database");
        if (database == null) {
            sendMessage("§c[Market] [ERROR] §fКонфиг не найден! Магазин работать не будет! Плагин отключен!");
            return;
        }

        this.shopManager = new ShopManager();
        this.auctionManager = new AuctionManager(this);

        reloadConfig();
        PlayerLoader.init();

        new PlayerListener(this);

        new ReloadConfigCommand(this);
        new ConvertCommand();
    }

    private void loadLimitAuction() {
        Map<Group, Integer> limits = new HashMap<>();

        for (String string : config.getStringList("limits")) {
            if (!string.contains(":"))
                continue;
            String[] split = string.split(":");
            String groupName = split[0];
            int limit = Integer.parseInt(split[1]);
            Group group = Group.getGroupByName(groupName);
            if (group == Group.DEFAULT)
                continue;
            limits.put(group, limit);
        }

        limits.put(Group.DEFAULT, config.getInt("limitDefault"));
        auctionManager.setLimitItemsToSell(limits);
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        auctionManager.saveConfig();

        loadLimitAuction();

        shopManager.clearAll();
        auctionManager.clearAll();

        loadShop();
        loadAuction();
    }

    private void loadAuction() {
        File file = new File(CoreUtil.getConfigDirectory() + "/auction.yml");
        if (!file.exists()) {
            sendMessage("§c[Market] [ERROR] §fКонфиг не найден! Аукцион работать не будет!");
            return;
        }

        ConfigManager configManager = new ConfigManager(file);
        FileConfiguration config = configManager.getConfig();

        auctionManager.setConfig(config);
        auctionManager.setFile(file);

        GamerManager gamerManager = NekoCloud.getGamerManager();

        ConfigurationSection section = config.getConfigurationSection("Items");
        if (section != null && section.getKeys(false) != null) {
            for (String uuidString : section.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);

                String patch = "Items." + uuidString + ".";
                IBaseGamer gamer = gamerManager.getOrCreate(config.getInt(patch + "owner"));
                ItemStack itemStack = config.getItemStack(patch + "item");
                int price = config.getInt(patch + "price");
                long date = config.getLong(patch + "date");
                AuctionItemImpl item = new AuctionItemImpl(auctionManager, uuid,
                        gamer, itemStack, price, new Timestamp(date));

                auctionManager.getAllItems().put(item.getID(), item);
            }
        }

        if (auctionCommand != null)
            return;

        auctionCommand = new AuctionCommand(auctionManager);
        //auctionManager.updateGuis();
    }

    @Override
    public void onDisable() {
        auctionManager.onDisable();
        PlayerLoader.getMySqlDatabase().close();
    }

    private void loadShop() {
        File file = new File(CoreUtil.getConfigDirectory() + "/shop.yml");
        if (!file.exists()) {
            sendMessage("§c[Market] [ERROR] §fКонфиг не найден! Магазин работать не будет!");
            return;
        }

        ConfigManager configManager = new ConfigManager(file);
        FileConfiguration config = configManager.getConfig();

        shopManager.getShopsNames().addAll(config.getConfigurationSection("Guis").getKeys(false));

        for (Language lang : Language.values()) {
            String absent = lang.getMessage("ABSENT");

            for (String name : config.getConfigurationSection("Guis").getKeys(false)) {
                String patch = "Guis." + name + ".";

                String nameGui = config.getString(patch + "nameGui");
                List<ShopItem> items = new ArrayList<>();
                for (String slotString : config.getConfigurationSection(patch + "items").getKeys(false)) {
                    String patchItem = patch + "items." + slotString + ".";

                    int slot = Integer.parseInt(slotString);
                    Material material = Material.valueOf(config.getString(patchItem + "material"));
                    short damage = 0;
                    if (config.contains(patchItem + "damage"))
                        damage = (short) config.getInt(patchItem + "damage");

                    int amount = config.getInt(patchItem + "amount");
                    double buyPrice = config.getDouble(patchItem + "buyPrice");
                    double sellPrice = config.getDouble(patchItem + "sellPrice");

                    final ItemStack item = new ItemStack(material, amount, damage);

                    ItemStack itemStack = ItemUtil.getBuilder(item.clone())
                            .setLore(lang.getList("SHOP_LORE_ITEM",
                                    buyPrice > 0 ? round(buyPrice * amount) + "$" : absent,
                                    sellPrice > 0 ? round(sellPrice * amount) + "$" : absent,
                                    material.toString()))
                            .setAmount(amount)
                            .build();

                    items.add(new ShopItem(slot, itemStack, amount, buyPrice, sellPrice, name, item));
                }

                ShopGui shopGui = new ShopGui(lang, name, nameGui, items);
                shopManager.addShop(shopGui);
            }
        }

        if (shopCommand != null)
            return;
        shopCommand = new ShopCommand(shopManager);
    }

    public static double round(double d) {
        return d > 0 ? Math.floor((d * 100) + 0.000001) / 100 : Math.ceil(d * 100) / 100;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public static String getDatabase() {
        return database;
    }

    private void sendMessage(String message) {
        GamerManager gamerManager = NekoCloud.getGamerManager();
        if (gamerManager == null)
            return;
        Spigot spigotServer = gamerManager.getSpigot();
        spigotServer.sendMessage(message);
    }
}
