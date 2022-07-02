package me.nekocloud.market.command;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.market.api.AuctionItem;
import me.nekocloud.market.auction.AuctionItemImpl;
import me.nekocloud.market.auction.AuctionManager;
import me.nekocloud.market.auction.gui.AuctionMyGui;
import me.nekocloud.market.auction.gui.AuctionPlayerGui;
import me.nekocloud.market.utils.MarketUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class AuctionCommand implements CommandInterface {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    private final List<Material> materialList = Arrays.asList(
            Material.SHULKER_SHELL, Material.BLACK_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
            Material.SILVER_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.RED_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX,
            Material.ENCHANTED_BOOK, Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION
    );

    private final AuctionManager manager;

    public AuctionCommand(AuctionManager manager) {
        this.manager = manager;


        SpigotCommand spigotCommand = COMMANDS_API.register("ah", this,
                "auction", "аукцион", "auc");
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (LocationUtil.distance(player.getLocation(), LocationUtil.stringToLocation(manager.getConfig()
                        .getString("auctionLocation"), false)) > 30.0) {
            gamer.sendMessageLocale("AUCTION_ANOTHER_LOCATION");
            return;
        }

        if (args.length < 1) {
            manager.openMainGui(gamer);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "show":
                if (args.length < 2) {
                    AuctionMyGui auctionMyGui = manager.getOrCreateMyGui(player);
                    auctionMyGui.open();
                    return;
                }

                String who = args[1];
                BukkitUtil.runTaskAsync(() -> {
                    IBaseGamer gamerWho = gamerManager.getGamer(who);
                    if (gamerWho == null) {
                        if (GlobalLoader.containsPlayerID(who) == -1) {
                            COMMANDS_API.playerNeverPlayed(gamerEntity, who);
                            return;
                        }
                        gamerWho = gamerManager.getOrCreate(who);
                    }

                    IBaseGamer finalGamerWho = gamerWho;
                    int count = (int) manager.getAllItems().values().stream()
                            .filter(auctionItem -> auctionItem.getOwner().getPlayerID() == finalGamerWho.getPlayerID())
                            .filter(auctionItem -> !auctionItem.isExpired())
                            .count();
                    if (count < 1) {
                        gamer.sendMessageLocale("AUCTION_SHOW_ERROR", who);
                        return;
                    }

                    if (player == null || !player.isOnline())
                        return;

                    AuctionPlayerGui gui = manager.getPlayerGui(gamer, gamerWho);
                    BukkitUtil.runTask(() -> gui.open(player));
                });

                break;
            case "sell":
            case "buy":
                if (args.length < 2) {
                    COMMANDS_API.notEnoughArguments(gamerEntity,"SERVER_PREFIX",  "AUCTION_FORMAT");
                    return;
                }

                if (!manager.checkLimit(gamer)) {
                    gamer.sendMessageLocale("AUCTION_LIMIT_ERROR", manager.getLimit(gamer));
                    return;
                }

                ItemStack itemHand = player.getInventory().getItemInMainHand();
                if (itemHand.getType() == Material.AIR) {
                    gamer.sendMessageLocale("AUCTION_SELL_AIR");
                    return;
                }
                Integer price = parseInt(args[1]);
                if (price == null) {
                    gamer.sendMessageLocale("AUCTION_COMMAND_ERROR");
                    return;
                }

                if (price < 1) {
                    gamer.sendMessageLocale("AUCTION_COMMAND_ERROR1");
                    return;
                }

                ItemStack itemStack = itemHand.clone();
                Integer amount = itemStack.getAmount();
                if (args.length > 2) {
                     amount = parseInt(args[2]);
                     if (amount == null) {
                         gamer.sendMessageLocale("AUCTION_COMMAND_ERROR2");
                         return;
                     }
                     if (itemHand.getAmount() < amount) {
                         gamer.sendMessageLocale("AUCTION_ADD_TO_SELL_ERROR");
                         return;
                     }
                     if (amount < 1) {
                         gamer.sendMessageLocale("AUCTION_COMMAND_ERROR1");
                         return;
                     }
                     itemStack.setAmount(amount);
                }

                PlayerInventory playerInventory = player.getInventory();
                if ((playerInventory.getItemInOffHand() != null
                        && playerInventory.getItemInOffHand().getType() == itemStack.getType())
                        || (playerInventory.getHelmet() != null
                        && playerInventory.getHelmet().getType() == itemStack.getType())) {
                    gamer.sendMessageLocale("AUCTION_COMMAND_ERROR3");
                    return;
                }
                AuctionItem auctionItem = new AuctionItemImpl(manager, UUID.randomUUID(),
                        gamer, itemStack, price, new Timestamp(System.currentTimeMillis()));
                manager.add(auctionItem);
                gamer.sendMessageLocale("AUCTION_ADD_TO_SELL",
                        itemStack.getType().toString(), StringUtil.getNumberFormat(price));

                if (materialList.contains(itemHand.getType())) {
                    removeItem(player, itemHand);
                    //player.getInventory().remove(itemHand);
                    return;
                }
                MarketUtil.removeItems(player, itemStack, amount);

                break;
            default:
                COMMANDS_API.showHelp(gamerEntity, "/ah", "AUCTION_COMMAND_HELP");
                break;
        }
    }

    private void removeItem(Player player, ItemStack removed) {
        ItemStack[] items = player.getInventory().getStorageContents();

        for(int i = 0; i < items.length; ++i) {
            if (items[i] != null && items[i].equals(removed)) {
                player.getInventory().clear(i);
                break;
            }
        }
    }

    private Integer parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return null;
        }
    }
}
