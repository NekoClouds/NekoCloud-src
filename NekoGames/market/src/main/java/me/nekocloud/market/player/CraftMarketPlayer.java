package me.nekocloud.market.player;

import lombok.Getter;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.utils.PlayerLoader;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Getter
public class CraftMarketPlayer implements MarketPlayer {

    private final IBaseGamer gamer;
    private double money;

    public CraftMarketPlayer(@NotNull IBaseGamer gamer, double money) {
        this.gamer = gamer;
        this.money = money;
    }

    @Override
    public boolean hasMoney(double money) {
        return this.money >= money;
    }

    @Override
    public int getPlayerID() {
        return gamer.getPlayerID();
    }

    @Override @Nullable
    public Player getPlayer() {
        return null; //не требуется!
    }

    @Override
    public String getName() {
        return gamer.getName();
    }

    @Override
    public boolean changeMoney(double money) {
        if (this.money + money < 0)
            return false;

        this.money += money;
        PlayerLoader.updateMoney(this, money);
        return true;
    }
}
