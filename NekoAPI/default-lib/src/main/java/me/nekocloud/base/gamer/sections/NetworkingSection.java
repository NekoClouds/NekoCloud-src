package me.nekocloud.base.gamer.sections;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.sql.PlayerInfoLoader;
import me.nekocloud.base.util.Pair;

import java.util.Map;

@Getter
public class NetworkingSection extends Section {

    private final Object2IntMap<KeyType> keys = new Object2IntOpenHashMap<>();
    private final Object2IntMap<KeyType> random = new Object2IntOpenHashMap<>();

    private int giveRewardLevel = 0; //за какой последний уровень выдана награда

    private int exp = 0;
    @Setter
    private int level = 0;
    private int expNextLevel = 1000000;

    public NetworkingSection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    public int getKeys(KeyType keyType) {
        return keys.getOrDefault(keyType, 0);
    }

    @Override
    public boolean loadData() {
        int playerID = baseGamer.getPlayerID();

        Pair<Pair<Integer, Integer>, Map<KeyType, Pair<Integer, Integer>>> data = PlayerInfoLoader.getData(playerID);
        this.exp = data.getFirst().getFirst();
        this.giveRewardLevel = data.getFirst().getSecond();
        this.level = checkLVL(exp);

        for (Map.Entry<KeyType, Pair<Integer, Integer>> keysData : data.getSecond().entrySet()) {
            KeyType keyType = keysData.getKey();
            int value = keysData.getValue().getFirst();
            int random = keysData.getValue().getSecond();
            this.keys.put(keyType, value);
            this.random.put(keyType, random);
        }

        return true;
    }

    public void updateGiveRewardLevel() { //обновить инфу о том, что игрок получил награду за какой-то уровень
        this.giveRewardLevel += 1;

        PlayerInfoLoader.updateData(baseGamer.getPlayerID(),
                PlayerInfoLoader.EXP_KEY,
                0,
                giveRewardLevel,
                false);
    }

    public void updateKeys(KeyType keyType, int keys, boolean update) { //для кора или баккита (для синхронизации)
        if (update) {
            this.keys.put(keyType, this.keys.getOrDefault(keyType, 0) + keys);
            return;
        }

        this.keys.put(keyType, keys);

    }

    public boolean addExp(int exp) {
        PlayerInfoLoader.updateData(baseGamer.getPlayerID(),
                PlayerInfoLoader.EXP_KEY,
                exp,
                giveRewardLevel,
                this.exp == 0);
        this.exp += exp;

        int newLevel = checkLVL(this.exp);
        if (newLevel > this.level) {
            this.level = newLevel;
            return true;
        }

        return false;
    }

    /**
     * при открытии кейсов нужно вызывать этот метод, а не через bukkitGamer.changeKeys
     * (это нужно для псевдорандома)
     * @param keyType = тип ключа
     * @param newRandomNumber - новое число для рандома
     */
    public void openKeys(KeyType keyType, int newRandomNumber) {
        int value = this.keys.getOrDefault(keyType, 0);
        if (value -1 < 0) {
            return;
        }

        PlayerInfoLoader.updateData(baseGamer.getPlayerID(),
                keyType.getId(),
                -1,
                newRandomNumber,
                !this.keys.containsKey(keyType));

        this.keys.put(keyType, value - 1);
        this.random.put(keyType, newRandomNumber);
    }

    public boolean changeKeys(KeyType keyType, int amount) {
        int value = this.keys.getOrDefault(keyType, 0);
        if (value + amount < 0) {
            return false;
        }

        int random = this.random.getOrDefault(keyType, 0);

        PlayerInfoLoader.updateData(baseGamer.getPlayerID(),
                keyType.getId(),
                amount,
                random,
                !this.keys.containsKey(keyType));
        this.keys.put(keyType, value + amount);
        return true;
    }

    private int checkLVL(int exp) {
        for (int i = 1;;i++) {
            if (exp == 0) {
                expNextLevel = checkXPLVL(i);
                return 0;
            }

            if (checkXPLVL(i) == exp) {
                expNextLevel = checkXPLVL(i + 1);
                return i;
            }

            if (checkXPLVL(i) > exp) {
                expNextLevel = checkXPLVL(i) - exp;
                return i - 1;
            }

            exp -= checkXPLVL(i);
        }
    }

    public static int checkXPLVL(int level) {
        return 100 * 2 * level;
    }

    public static int getCurrentXPLVL(int exp) {
        if (exp == 0) {
            return 0;
        }

        for (int i = 0;;i++) {
            if (checkXPLVL(i) >= exp) {
                return exp;
            }

            exp -= checkXPLVL(i);
        }
    }

    public void setKeysToMysql(KeyType keyType, int keys) {
        if (keys < 0) {
            return;
        }
        PlayerInfoLoader.setData(baseGamer.getPlayerID(), keyType.getId(), keys, !this.keys.containsKey(keyType));
        this.keys.put(keyType, keys);
    }
}
