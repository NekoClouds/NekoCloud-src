package me.nekocloud.base.gamer.sections;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MoneySection extends Section {

    private final Map<PurchaseType, Integer> data = new ConcurrentHashMap<>();

    @Setter
    private double multiple;

    public MoneySection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    @Override
    public boolean loadData() {
        this.data.putAll(GlobalLoader.getPlayerMoney(baseGamer.getPlayerID()));

        this.multiple = getMultiple(baseGamer.getGroup());

        return true;
    }

    public int getMoney(PurchaseType purchaseType) {
        return this.data.getOrDefault(purchaseType, 0);
    }

    public static double getMultiple(@NotNull Group group) {
        int levelGroup = group.getLevel();
        double multiple = 1.0;
        if (levelGroup >= Group.HEGENT.getLevel()) {
            multiple = 1.5; //hegent
        }
        if (levelGroup >= Group.TRIVAL.getLevel()) {
            multiple = 1.75; //trival
        }
        if (levelGroup >= Group.AXSIDE.getLevel()) {
            multiple = 2.0; //axside
        }
        return multiple;
    }

    public boolean changeMoney(PurchaseType purchaseType, int money) {
        int value = getMoney(purchaseType);
        if (value + money < 0) {
            return false;
        }

        GlobalLoader.changeMoney(baseGamer.getPlayerID(), purchaseType, money, !data.containsKey(purchaseType));

        this.data.put(purchaseType, value + money);

        return true;
    }

    public void updateMoneyForCore(PurchaseType purchaseType, int money, boolean update) {
        if (update) {
            this.data.put(purchaseType, this.getMoney(purchaseType) + money);
            return;
        }
        this.data.put(purchaseType, money);
    }

    public void setMoneyToMysql(PurchaseType purchaseType, int money) {
        if (money < 0) {
            return;
        }

        GlobalLoader.setMoney(baseGamer.getPlayerID(), purchaseType, money, !this.data.containsKey(purchaseType));
        this.data.put(purchaseType, money);
    }


    static {
        new TableConstructor("players_money",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true).autoIncrement(true),
                new TableColumn("playerId", ColumnType.INT_11).primaryKey(true),
                new TableColumn("typeMoney", ColumnType.INT_5).primaryKey(true),
                new TableColumn("value", ColumnType.INT_5).setDefaultValue(0)
        ).create(GlobalLoader.getMysqlDatabase()); // + добавить индексы
    }


}
