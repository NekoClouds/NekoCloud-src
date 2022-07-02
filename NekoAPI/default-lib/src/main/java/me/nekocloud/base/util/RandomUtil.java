package me.nekocloud.base.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public final class RandomUtil {
    private static final Random RANDOM = new Random();

    public static int getInt(int min2, int max) {
        int diff = max - min2;
        return RANDOM.nextInt(diff + 1) + min2;
    }

    public static int getInt(int random) {
        return RANDOM.nextInt(random);
    }

    public static boolean getBoolean() {
        return RANDOM.nextBoolean();
    }

    public static int getDouble(double min2, double max) {
        double diff = max - min2;
        return (int) (RANDOM.nextDouble(diff + 1.0D) + min2);
    }
}

