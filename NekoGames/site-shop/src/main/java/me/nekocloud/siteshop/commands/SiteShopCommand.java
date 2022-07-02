package me.nekocloud.siteshop.commands;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import me.nekocloud.siteshop.SiteShop;
import me.nekocloud.siteshop.guis.BuySGui;
import me.nekocloud.siteshop.item.SSItemManager;
import org.bukkit.entity.Player;

public final class SiteShopCommand implements CommandInterface {

    private final TIntObjectMap<BuySGui> shopGuis = new TIntObjectHashMap<>();
    private final SSItemManager ssItemManager;

    public SiteShopCommand(SiteShop siteShop) {
        this.ssItemManager = siteShop.getItemManager();

        for (Language language : Language.values()) {
            BuySGui buySGui = new BuySGui(language);
            buySGui.setItems(ssItemManager);
            shopGuis.put(language.getId(), buySGui);
        }

        String nameCommand = "siteshop";
        COMMANDS_API.disableCommand(nameCommand);

        SpigotCommand command = COMMANDS_API.register(nameCommand, this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        BuySGui buySGui = shopGuis.get(gamer.getLanguage().getId());
        if (buySGui != null) {
            buySGui.open(player);
            return;
        }

        shopGuis.get(Language.DEFAULT.getId()).open(player);
    }
}
