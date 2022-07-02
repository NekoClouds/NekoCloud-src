package me.nekocloud.siteshop.commands;

import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.siteshop.SiteShop;
import me.nekocloud.siteshop.guis.SSGui;

public final class GiveCommand implements CommandInterface {

    private final SiteShop siteShop;

    public GiveCommand(SiteShop siteShop) {
        this.siteShop = siteShop;

        SpigotCommand command = COMMANDS_API.register("give", this, "выдать");
        command.setOnlyPlayers(true);
        command.setCooldown(40, this.toString());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;

        if (strings.length > 0 && gamer.isDeveloper()) {
            if (strings[0].equalsIgnoreCase("reload")) {
                siteShop.reloadConfig();
                gamer.sendMessage("§d§lNekoCloud §8| §fКонфиг плагина SiteShop перезагружен, вещи обнавлены");
            }
        }

        BukkitUtil.runTaskLater(20L, () -> {
            SSGui ssGui = new SSGui(siteShop.getItemManager(), gamer);
            ssGui.loadFromMysql();
            ssGui.updateGui();
            BukkitUtil.runTask(ssGui::open);
        });
    }
}
