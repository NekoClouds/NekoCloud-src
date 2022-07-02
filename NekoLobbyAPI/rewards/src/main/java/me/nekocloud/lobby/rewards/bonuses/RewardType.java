package me.nekocloud.lobby.rewards.bonuses;

import lombok.Getter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Getter
public enum RewardType {

    FIRST(0, TimeUnit.DAYS.toMillis(1), "FIRST_REWARD_KEY", "DAILY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus()),
    SECOND(1, TimeUnit.DAYS.toMillis(2), "SECOND_REWARD_KEY", "WEEKLY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus(), new KeysBonus()),
    THIRD(3, TimeUnit.DAYS.toMillis(3), "THIRD_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus(), new KeysBonus()),
    FOURTH(4, TimeUnit.DAYS.toMillis(4), "FOURTH_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus(), new KeysBonus(), new KeysBonus()),
    FIFTH(5, TimeUnit.DAYS.toMillis(5), "FIFTH_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus(), new KeysBonus(), new KeysBonus()),
    SIXTH(6, TimeUnit.DAYS.toMillis(6), "SIXTH_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus(), new KeysBonus(), new KeysBonus(), new KeysBonus()),
    SEVENTH(7, TimeUnit.DAYS.toMillis(7), "SEVENTH_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY",
            new MoneyBonus(), new ExpBonus(), new KeysBonus(), new KeysBonus(), new KeysBonus(), new VirtsBonus()),
    EIGHTH(8, TimeUnit.DAYS.toMillis(8), "", "",
            new KeysBonus()),
    NIGHT(9, TimeUnit.DAYS.toMillis(9), "", "",
            new KeysBonus()),
    TEN(10, TimeUnit.DAYS.toMillis(10), "", "",
            new KeysBonus()),
    ELEVENS(11, TimeUnit.DAYS.toMillis(11), "", "",
            new KeysBonus()),
    TWELVES(12, TimeUnit.DAYS.toMillis(12), "", "",
            new KeysBonus()),
    THIRDS(13, TimeUnit.DAYS.toMillis(13), "", "",
            new KeysBonus()),
    FOURTHS(14, TimeUnit.DAYS.toMillis(14), "", "",
            new KeysBonus()),
    FIFTHS(15, TimeUnit.DAYS.toMillis(15), "", "",
            new KeysBonus()),
    SIXTHS(16, TimeUnit.DAYS.toMillis(16), "", "",
            new KeysBonus()),
    SEVENTHS(17, TimeUnit.DAYS.toMillis(17), "", "",
            new KeysBonus()),
    EIGHTHS(18, TimeUnit.DAYS.toMillis(18), "", "",
            new KeysBonus()),
    NIGHTS(19, TimeUnit.DAYS.toMillis(19), "", "",
            new KeysBonus()),
    TWENTIETH(20, TimeUnit.DAYS.toMillis(20), "", "",
            new KeysBonus()),
    TWENTY_FIRST(21, TimeUnit.DAYS.toMillis(21), "", "",
            new KeysBonus()),
    TWENTY_SECOND(22, TimeUnit.DAYS.toMillis(22), "", "",
            new KeysBonus()),
    TWENTY_THIRD(23, TimeUnit.DAYS.toMillis(23), "", "",
            new KeysBonus()),
    TWENTY_FOURTH(24, TimeUnit.DAYS.toMillis(24), "", "",
            new KeysBonus()),
    TWENTY_FIFTH(25, TimeUnit.DAYS.toMillis(25), "", "",
            new KeysBonus()),
    TWENTY_SIXTH(26, TimeUnit.DAYS.toMillis(26), "", "",
            new KeysBonus()),
    TWENTY_SEVENTH(27, TimeUnit.DAYS.toMillis(27), "", "",
            new KeysBonus()),
    TWENTY_EIGHTH(28, TimeUnit.DAYS.toMillis(28), "", "",
            new KeysBonus()),
    TWENTY_NIGHT(29, TimeUnit.DAYS.toMillis(29), "", "",
            new KeysBonus()),
    THIRTIETH(30, TimeUnit.DAYS.toMillis(30), "", "",
            new KeysBonus()),
    ;

    private final int id;
    private final long timeDelay;
    private final String localeKey;
    private final String localeLoreKey;
    private final Bonus[] rewards;

    public int countRewards() {
        return rewards.length;
    }

    RewardType(int id, long timeDelay, String localeKey, String localeLoreKey, Bonus... rewards) {
        this.id = id;
        this.timeDelay = timeDelay;
        this.localeKey = localeKey;
        this.localeLoreKey = localeLoreKey;
        this.rewards = rewards;
    }

    public static RewardType getRewardType(int id) {
        return Arrays.stream(values()).filter(rewardType -> rewardType.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
