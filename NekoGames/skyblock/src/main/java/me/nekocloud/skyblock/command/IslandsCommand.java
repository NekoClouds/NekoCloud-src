package me.nekocloud.skyblock.command;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.locale.Language;
import me.nekocloud.skyblock.gui.general.IslandsOpenedGui;
import org.bukkit.entity.Player;

public final class IslandsCommand implements CommandInterface {

    private static final int DEFAULT_LANGUAGE_ID = Language.DEFAULT.getId();

    private final TIntObjectMap<IslandsOpenedGui> inventorys = new TIntObjectHashMap<>();
    private int cooldown = 60;

    public IslandsCommand() {
        for (Language language : Language.values())
            inventorys.put(language.getId(), new IslandsOpenedGui(language));

        SpigotCommand command = NekoCloud.getCommandsAPI().register("islands", this,
                "skyblocks", "острова");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Language lang = gamer.getLanguage();
        Player player = gamer.getPlayer();

        IslandsOpenedGui gui = inventorys.get(lang.getId());
        if (gui != null) {
            gui.open(player);
            return;
        }

        inventorys.get(DEFAULT_LANGUAGE_ID).open(player);
    }

    public void update() {
        cooldown--;
        if (cooldown < 1) {
            for (IslandsOpenedGui gui : inventorys.valueCollection()) {
                gui.update();
            }
            cooldown = 60;
        }
    }
}
