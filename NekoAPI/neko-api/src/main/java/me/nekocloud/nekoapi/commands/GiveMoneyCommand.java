package me.nekocloud.nekoapi.commands;

import com.google.common.collect.ImmutableList;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class GiveMoneyCommand implements CommandInterface, CommandTabComplete {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();
    private final ImmutableList<String> immutableList = ImmutableList.of("virt", "coin", "rub", "rmb");

    public GiveMoneyCommand() {
        SpigotCommand command = COMMANDS_API.register("addmoney", this);
        command.setCommandTabComplete(this);
        command.setGroup(Group.ADMIN);
    }

    @Override
	public void execute(
			final @NotNull GamerEntity gamerEntity,
			final String command,
			final String @NotNull[] args
	) {
        if (gamerEntity.isHuman() && !((BukkitGamer)gamerEntity).isDeveloper())
            return;

        if (args.length < 3) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "/addmoney <ник игрока> <тип> <монеты>");
            return;
        }

        int money;
        try {
            money = Integer.parseInt(args[2]);
        } catch (Exception e) {
            money = 0;
        }

        PurchaseType purchaseType;
        try {
            purchaseType = PurchaseType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            purchaseType = PurchaseType.COINS;
        }

        addMoney(gamerEntity, args[0], purchaseType, money);
    }

    private void addMoney(GamerEntity gamerEntity, String name, PurchaseType purchaseType, int money) {
        BukkitUtil.runTaskAsync(() -> {
            IBaseGamer gamer = gamerManager.getOrCreate(name);
            if (gamer == null || gamer.getPlayerID() == -1) {
                COMMANDS_API.playerNeverPlayed(gamerEntity, name);
                return;
            }

            if (!gamer.isOnline()) {
                Map<PurchaseType, Integer> playerMoney = GlobalLoader.getPlayerMoney(gamer.getPlayerID());
                GlobalLoader.changeMoney(gamer.getPlayerID(), purchaseType, money, !playerMoney.containsKey(purchaseType));


//                BukkitBalancePacket packet = new BukkitBalancePacket(gamer.getPlayerID(),
//                        BukkitBalancePacket.Type.COINS,
//                        purchaseType.getId(),
//                        money,
//                        true);
//                ConnectorPlugin.instance().getConnector().sendPacketAsync(packet);
            } else if (gamer instanceof BukkitGamer targetGamer) {
                targetGamer.changeMoney(purchaseType, money);
            }

            gamerEntity.sendMessage("§d§lСЕРВЕР §8| §fТы выдал игроку " + gamer.getDisplayName() +
                    " " + purchaseType.getColor()
                    + StringUtil.getNumberFormat(money)
                    + " " + purchaseType.name());
        });
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String alias, String[] args) {
        if (gamerEntity.isHuman() && !((BukkitGamer)gamerEntity).isDeveloper()) {
            return ImmutableList.of();
        }

        if (args.length == 2) {
            return COMMANDS_API.getCompleteString(immutableList, args);
        }

        return ImmutableList.of();
    }
}
