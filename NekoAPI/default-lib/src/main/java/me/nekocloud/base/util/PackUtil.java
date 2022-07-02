package me.nekocloud.base.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PackUtil {

    /**
     *
     * @param value - значение
     * @param amount - кол-во элементов
     * @param states - макс число
     * @return вернуть все числа
     */
    public int[] unPack(long value, int amount, int states) {
        int bits = getBitSize(states - 1);
        int bitmask = getBitMask(bits);

        int[] values = new int[amount];

        for (int part = 0; part < amount; part++) {
            values[part] = (int)(value & bitmask);
            value >>= bits;
        }
        return values;
    }

    public long pack(int states, int @NotNull ... values) {
        long value = 0;
        int bits = getBitSize(states - 1);
        for (int part = values.length - 1; part >= 0; part--) {
            value <<= bits;
            value += values[part];
        }

        return value;
    }

    private int getBitSize(int states) {
        int n = 0;
        while (states > 0) {
            states >>= 1;
            n++;
        }
        return n;
    }

    private int getBitMask(int bits) {
        int mask = 1;
        for (int i = 0; i < bits; i++)
            mask <<= 1;

        mask -= 1;
        return mask;
    }
}
