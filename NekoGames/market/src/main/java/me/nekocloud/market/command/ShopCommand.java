package me.nekocloud.market.command;

import me.nekocloud.api.JSONMessageAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.JsonBuilder;
import me.nekocloud.market.shop.ShopGui;
import me.nekocloud.market.shop.ShopManager;
import org.bukkit.entity.Player;

public final class ShopCommand implements CommandInterface {

    private final JSONMessageAPI jsonMessageAPI = NekoCloud.getJsonMessageAPI();

    private final ShopManager shopManager;

    public ShopCommand(ShopManager shopManager) {
        this.shopManager = shopManager;

        SpigotCommand spigotCommand = COMMANDS_API.register("shop", this, "магазин");
        spigotCommand.setOnlyPlayers(true);
    }

    public void execute(GamerEntity gamerEntity, String cmd, String[] args) {
        Language lang = gamerEntity.getLanguage();
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (args.length < 2 && !gamer.isAkio()) {
            gamerEntity.sendMessageLocale("NO_PERMS_GROUP", Group.AKIO.getNameEn());
            return;
        }

        if (args.length > 1 && !args[1].equalsIgnoreCase("npckek")) {
            gamerEntity.sendMessageLocale("NO_PERMS_GROUP", Group.AKIO.getNameEn());
            return;
        }

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity,"SERVER_PREFIX",  "SHOP_FORMAT");
            JsonBuilder names = new JsonBuilder();
            names.addText(lang.getMessage("SHOP_NAMES"));
            int amountShop = 0;
            for (String name : shopManager.getShopsNames()) {
                amountShop++;
                names.addRunCommand("§a" + name, "/shop " + name,
                        lang.getMessage("SHOP_HOVER_TEST"));
                if (amountShop < shopManager.getShopsNames().size())
                    names.addText("§f, ");

            }
            jsonMessageAPI.send(player, names);
            return;
        }

        String guiName = args[0].toLowerCase();
        ShopGui shopGui = shopManager.getGuis().get(guiName + lang);
        if (shopGui == null) {
            gamer.sendMessageLocale("SHOP_NOT_FOUND", guiName);
            return;
        }

        shopGui.getDInventory().openInventory(player);
    }
}
